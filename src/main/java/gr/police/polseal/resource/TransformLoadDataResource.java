package gr.police.polseal.resource;

import gr.police.polseal.service.JwtService;
import gr.police.polseal.service.TransformLoadDataService;
import gr.police.polseal.service.utils.GeneralUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;


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

    private final JwtService jwtService;


    @POST
    @Path("create-new-minio-user")
    public Response createMinioUser(@QueryParam("username") String username, @QueryParam("password") String password) throws IOException {
        int responseCode = transformLoadDataService.createNewMinioUser(username, password);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return Response.status(Response.Status.OK).entity("User created successfully").build();
        } else {
            return Response.status(Response.Status.OK).entity("Failed to create user. Response code:" + responseCode).build();
        }
    }


    @POST
    @Path("load-file-to-bucket")
    public Response loadFile(String csvAsBase64, @QueryParam("fileId") String fileId, @QueryParam("organisation") String tenantId) throws Exception {

//        todo na mpei to tenantId apo to token
//        String tenantId = String.valueOf(jwtService.getUserId());


        // Decode the Base64 string to byte array
        byte[] decodedBytes = Base64.getDecoder().decode(csvAsBase64);
        // Create a temporary CSV file
        String tempName = tenantId + "_" + fileId;
        File csvFile = new File(tempName + ".csv");
        FileUtils.writeByteArrayToFile(csvFile, decodedBytes);

        //loading the csv file into the raw-data bucket of min io
        transformLoadDataService.loadFileTOBucket(tempName, tenantId);

//        we calculate the csvHeaders and their type (VARCHAR, INTEGER or TIMESTAMP)
//        in order to use them to our create and insert trino queries
        String[] csvHeaders = GeneralUtils.extractingCSVHeaders(tempName);
        String[] headerType = transformLoadDataService.determineColumnType(tempName);


        if (csvHeaders != null && csvHeaders.length > 0) {

            boolean successfulSchemaCreation = transformLoadDataService.createHiveSchema(tenantId );
            boolean successfulTempTableCreation = false;
            boolean successfulOrcTableCreation = false;
            boolean successfulInsertion = false;

//            if the schema is there, we are creating the temp hive table based on the csv
            if (successfulSchemaCreation) {
                successfulTempTableCreation = transformLoadDataService.createTempHiveTable(tenantId, csvHeaders);
            }

//            if the temp table is there, we are creating the orc table based on it
            if (successfulTempTableCreation) {
                successfulOrcTableCreation = transformLoadDataService.createOrcHiveTable(tenantId, csvHeaders, headerType);
            }

//            if the orc table is created correctly, then we are inserting the data
            if (successfulOrcTableCreation) {
                successfulInsertion = transformLoadDataService.insertIntoOrcTable(tenantId, csvHeaders, headerType);
            }
//            we return the state of the transaction based on the success or not of the insertion
            if (successfulInsertion) {
                return Response.status(Response.Status.OK).entity("File loaded successfully and inserted into the orc table").build();
            } else {
                return Response.status(Response.Status.OK).entity("Something went wrong. Data hasn't been inserted to the orc table").build();
            }
        }
        return null;
    }


}
