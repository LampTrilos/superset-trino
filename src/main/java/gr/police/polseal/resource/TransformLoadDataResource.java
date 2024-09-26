package gr.police.polseal.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.police.polseal.service.TransformLoadDataService;
import gr.police.polseal.service.utils.GeneralUtils;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;


@Slf4j
@Singleton
@SecuritySchemes(value = {
        @SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.HTTP, scheme = "Bearer")
})
@SecurityRequirement(name = "apiKey")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class TransformLoadDataResource {


    private final TransformLoadDataService transformLoadDataService;

    //    the directory from which the example json will be pulled in order to check the headers
    private static String MODEL_EXAMPLE_DIRECTORY = "src/main/resources/model-examples/";

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("load-file-to-bucket")
    public Response loadFile(String sentFileAsBase64, @QueryParam("fileId") String fileId) throws Exception {
        // Remove leading and trailing quotes
        sentFileAsBase64 = sentFileAsBase64.replaceAll("^\"+|\"+$", "");
        String[] csvHeaders = new String[0];

//        we get the tenantId from the roles attribute of the sent JWT token
//        String tenantId = jwt.getClaim("roles").toString().replace("\"", "")
//                .replace("[", "").replace("]", "");

        String tenantId = jwt.getClaim("realm_access").toString().replace("\"", "")
                .replace("[", "").replace("]", "");

        // Step 1: Remove all non-alphabetic characters except for spaces
        String cleanedInput = tenantId.replaceAll("[^a-zA-Z, ]", "");

        // Step 2: Split the string into words and filter out words that contain 'User' or 'quarkus'
        String[] roles = cleanedInput.split(",");
        StringBuilder filteredRoles = new StringBuilder();

        for (String role : roles) {
            // Only keep roles that do not contain 'User' or 'quarkus'
            if (!role.contains("User") && !role.contains("quarkus")) {
                filteredRoles.append(role).append(",");
            }
        }

        // Remove trailing comma if needed
        if (filteredRoles.length() > 0 && filteredRoles.charAt(filteredRoles.length() - 1) == ',') {
            filteredRoles.setLength(filteredRoles.length() - 1);
        }
        tenantId = filteredRoles.toString();

        if (tenantId != null && !tenantId.equalsIgnoreCase("")) {
            // Decode the Base64 string to byte array
            byte[] decodedBytes = Base64.getDecoder().decode(sentFileAsBase64);

            // Create a temporary name for the temp file
            String tempName = tenantId + "_" + fileId;

            if (fileId.contains(".json")) {
                // Load all example JSON files in the directory and extract the headers from sent JSON
                JsonNode decodedAsJsonNode = GeneralUtils.convertByteToJsonString(decodedBytes);

//                // Compare headers with all example JSON files
                List<String> exampleHeaders = transformLoadDataService.compareHeadersAndReturnExample(decodedAsJsonNode);

                if (exampleHeaders.isEmpty()) {
                    // No match found
                    return Response.status(Response.Status.BAD_REQUEST).entity("Headers do NOT Match any example file.").build();
                }

                // If a match is found, load the example headers for CSV generation
                csvHeaders = exampleHeaders.toArray(new String[0]);

                // Transform the JSON to CSV
                String finalCsv = GeneralUtils.convertJsonToCsv(decodedAsJsonNode);
                decodedBytes = finalCsv.getBytes(StandardCharsets.UTF_8);

                // Change the file name to .csv
                tempName = tempName.replace(".json", ".csv");
            } else {
                // Handle non-JSON files (CSV files)
                csvHeaders = GeneralUtils.extractingCSVHeaders(tempName);
            }

//            we save the file as a temp in memory file
            File tempFile = new File(tempName);
            FileUtils.writeByteArrayToFile(tempFile, decodedBytes);

            //loading the file into the raw-data bucket of min io
            MinioClient minioClient = transformLoadDataService.createMinioClient();
            transformLoadDataService.loadFileTOBucket(minioClient, tempName, tenantId);

//            we check for the header type of each column. This will be used when the insert query is built
            String[] headerType = transformLoadDataService.determineColumnType(tempName);

//           we delete the in memory created file
            Files.deleteIfExists(java.nio.file.Path.of(tempName));


            if (csvHeaders != null && csvHeaders.length > 0) {
                boolean successfulTempTableCreation = false;
                boolean successfulOrcTableCreation = false;
                boolean successfulInsertion = false;

//                   we drop the temp table
                transformLoadDataService.dropTempHiveTable(tenantId);
//            we are creating the temp hive table based on the csv
                successfulTempTableCreation = transformLoadDataService.createTempHiveTable(tenantId, csvHeaders, tempName);

                // Regular expression for any non-alphanumeric character. We split the fileId to only alphabetical letters
                String[] parts = fileId.split("[^a-zA-Z0-9]");
                String fileNameForTable = parts[0];

//            if the temp table is there, we are creating the orc table based on it
                if (successfulTempTableCreation) {
                    successfulOrcTableCreation = transformLoadDataService.createOrcHiveTable(tenantId, fileNameForTable, csvHeaders, headerType);
                }

//            if the orc table is created correctly, then we are inserting the data
                if (successfulOrcTableCreation) {
                    successfulInsertion = transformLoadDataService.insertIntoOrcTable(tenantId, fileNameForTable, csvHeaders, headerType);
                }
//            we return the state of the transaction based on the success or not of the insertion
                if (successfulInsertion) {
                    return Response.status(Response.Status.OK).entity("File loaded successfully and inserted into the orc table").build();
                } else {
                    return Response.status(Response.Status.OK).entity("Something went wrong. Data hasn't been inserted to the orc table").build();
                }
            }
        } else {
            return Response.status(Response.Status.OK).entity("There is no role for this user").build();
        }
        return null;
    }


}
