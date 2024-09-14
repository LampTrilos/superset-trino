package gr.police.polseal.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;
import eu.ubitech.bitt.core.domain.storage.service.StorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class TransformLoadDataService {


    private static final String RAW_BUCKET = "raw-data";


    private final StorageService storageService;
    @ConfigProperty(name = "minio.endpoint")
    private String minioendpoint;

    @ConfigProperty(name = "minio.endpoint.https")
    private String minioendpointHttps;

    @ConfigProperty(name = "minio_accesskey")
    private String minioaccesskey;

    @ConfigProperty(name = "minio_secretkey")
    private String miniosecretkey;

    @ConfigProperty(name = "trino_uri")
    private String trinoUri;


    //    we load the File into the  raw-data minio bucket based on the tenant id
    public void loadFileTOBucket(String tempName, String tenantId) throws Exception {
        putObjectOnBucket(tenantId, RAW_BUCKET,
                tempName, "csv");

    }
//
    public int createNewMinioUser(String newUser, String newSecret) throws IOException {
        // Admin credentials for MinIO
        String adminAccessKey = minioaccesskey;
        String adminSecretKey = miniosecretkey;

        // MinIO endpoint
        String minioEndpoint = minioendpointHttps;

        // Create the JSON request body
        String jsonInputString = "{\"accessKey\":\"" + newUser + "\", \"secretKey\":\"" + newSecret + "\"}";

        // URL for the MinIO Admin API endpoint to create users
        URL url = new URL(minioEndpoint + "/minio/admin/v3/add-user");

        // Set up HTTP connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // Set authorization header (admin credentials)
        String auth = adminAccessKey + ":" + adminSecretKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

        // Send the request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Check the response code
       return connection.getResponseCode();

    }


    public void putObjectOnBucket(String tenantiId, String bucketname, String objectname, String mimetype)
            throws Exception {

//        creation of the minio client
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioendpoint)
                .credentials(minioaccesskey, miniosecretkey)
                .build();

////        creation of the minio client
//        MinioClient minioClient = MinioClient.builder()
//                .endpoint(minioendpoint)
//                .credentials("testUser", "test123")
//                .build();


        boolean foundRawData = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketname).build());
        if (!foundRawData) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketname).build());
            System.out.println("Bucket " + bucketname + " created ");
        }
        minioClient.uploadObject(UploadObjectArgs.builder()
                .bucket(bucketname)
                .object(tenantiId + "/" + objectname + "." + mimetype)
                .filename(objectname + "." + mimetype)
                .build());

//        if it doesn't exist, we create the hive-warehouse
        boolean foundHiveWareHouse = minioClient.bucketExists(BucketExistsArgs.builder().bucket("hive-warehouse-"+tenantiId).build());
        if (!foundHiveWareHouse) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("hive-warehouse"+tenantiId).build());
            System.out.println("Bucket hive-warehouse-"+tenantiId +" created ");
        } else {
            System.out.println("Bucket hive-warehouse-"+tenantiId+" already exists.");
        }
    }

    public boolean createTempHiveTable(String tenantId, String[] csvHeaders) {

        StringBuilder sql = new StringBuilder("CREATE TABLE if not exists hive.hive_schema_"+tenantId+".temp_" + tenantId + "\n" +
                "(\n");
        for (int i = 0; i < csvHeaders.length - 2; i++) {
            sql.append(csvHeaders[i]).append(" VARCHAR,\n");
        }
        sql.append(csvHeaders[csvHeaders.length - 1]).append(" VARCHAR \n" +
                ")\n" +
                "WITH ( format = 'CSV',\n" +
                "    csv_separator = ',',\n" +
                "    external_location = 's3a://raw-data/" + tenantId + "',\n" +
                "    skip_header_line_count = 1)");

        String result = trinoProcessing(sql.toString(), tenantId);
        if (result.contains("state\":\"FINISHED")) {
            return true;
        } else {
            return false;
        }

    }

    public boolean createOrcHiveTable(String tenantId, String[] csvHeaders, String[] headerType) {

        StringBuilder sql = new StringBuilder("CREATE TABLE if not exists hive.hive_schema_"+tenantId+"."+ tenantId + "\n" +
                "(\n");
//        we iterate until length - 2 because we don't need the comma (,) at the length - 1
        for (int i = 0; i < csvHeaders.length - 2; i++) {
            sql.append(csvHeaders[i]).append(" " + headerType[i] + ",\n");
        }
        sql.append(csvHeaders[csvHeaders.length - 1]).append(" " + headerType[headerType.length - 1] + " \n" +
                ") WITH (\n" +
                "      format = 'ORC')"
        );

        String result = trinoProcessing(sql.toString(), tenantId);
        if (result.contains("state\":\"FINISHED")) {
            return true;
        } else {
            return false;
        }

    }


    public boolean insertIntoOrcTable(String tenantId, String[] csvHeaders, String[] headerType) {

        StringBuilder sql = new StringBuilder("INSERT INTO  hive.hive_schema_"+tenantId+"." + tenantId + "\n" +
                " SELECT ");
        for (int i = 0; i < csvHeaders.length - 2; i++) {
//            if the headerType is VARCHAR, then there is no need to try_cast
            if (headerType[i].equalsIgnoreCase("VARCHAR")) {
                sql.append(csvHeaders[i]).append(",\n");
            } else {
                sql.append("try_cast(" + csvHeaders[i]).append(" as " + headerType[i] + "),\n");
            }
        }
        if (headerType[headerType.length - 1].equalsIgnoreCase("VARCHAR")) {
//            if the headerType is VARCHAR, then there is no need to try_cast
            sql.append(csvHeaders[csvHeaders.length - 1]);
        } else {
            sql.append("try_cast(" + csvHeaders[csvHeaders.length - 1]).append(" as " + headerType[headerType.length - 1] + ")\n");
        }
        sql.append(" FROM hive.hive_schema.temp_" + tenantId);


        String result = trinoProcessing(sql.toString(), tenantId);
        if (result.contains("state\":\"FINISHED")) {
            return true;
        } else {
            return false;
        }

    }


    public String[] determineColumnType(String filename) {
        int rowsToCheck = 5;  // Number of rows to check for each column


        try (CSVReader reader = new CSVReader(new FileReader(filename + ".csv"))) {
            String[] headers = reader.readNext();  // Read headers
            String[] headerType = new String[headers.length];

            List<String[]> rows = new ArrayList<>();

            // Read first few rows
            for (int i = 0; i < rowsToCheck; i++) {
                String[] row = reader.readNext();
                if (row == null) break;
                rows.add(row);
            }

            // Check each column for data types
            for (int col = 0; col < headers.length; col++) {
                String detectedType = detectColumnType(rows, col);
                assert headerType != null;
                headerType[col] = detectedType;
            }
            return headerType;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Detect the type of a column based on sample values
    private static String detectColumnType(List<String[]> rows, int colIndex) {
        boolean isInteger = true;
        boolean isTimestamp = true;

        for (String[] row : rows) {
            String value = row[colIndex];

            // Check for INTEGER
            if (isInteger && !isInteger(value)) {
                isInteger = false;
            }

            // Check for TIMESTAMP
            if (isTimestamp && !isDate(value)) {
                isTimestamp = false;
            }

            // If neither, it's VARCHAR
            if (!isInteger && !isTimestamp) {
                return "VARCHAR";
            }
        }

        if (isInteger) return "INTEGER";
        if (isTimestamp) return "TIMESTAMP";

        return "VARCHAR";  // Default to VARCHAR
    }

    // Check if a value is an integer
    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Check if a value matches any of the date formats
    private static boolean isDate(String value) {
        List<DateTimeFormatter> DATE_FORMATS = List.of(
                // Date Only Formats
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),           // 2023-09-12
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),           // 09/12/2023
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),           // 12-09-2023
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),           // 2023/09/12
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),           // 12/09/2023
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),           // 12.09.2023
                DateTimeFormatter.ofPattern("MMMM d, yyyy"),         // September 12, 2023
                DateTimeFormatter.ofPattern("EEE, MMM d, yyyy"),     // Tue, Sep 12, 2023
                DateTimeFormatter.ofPattern("yyyyMMdd"),             // 20230912 (compact date)

                // Time Only Formats
                DateTimeFormatter.ofPattern("HH:mm:ss"),             // 14:30:45 (24-hour clock)
                DateTimeFormatter.ofPattern("hh:mm:ss a"),           // 02:30:45 PM (12-hour clock with AM/PM)
                DateTimeFormatter.ofPattern("HH:mm"),                // 14:30
                DateTimeFormatter.ofPattern("hh:mm a"),              // 02:30 PM

                // Date and Time Formats
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),  // 2023-09-12 14:30:45
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),// 2023-09-12T14:30:45 (ISO 8601)
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),  // 09/12/2023 14:30:45
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),  // 12-09-2023 14:30:45
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),     // 2023-09-12 14:30
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),  // 12/09/2023 14:30:45

                // Time Zone Formats
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"), // 2023-09-12T14:30:45.123+0000
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"), // 2023-09-12T14:30:45.123+00:00
                DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z"),  // Tue, 12 Sep 2023 14:30:45 GMT
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),  // 2023-09-12T14:30:45.123Z (UTC)

                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")  // 2010-08-24 18:31:35.0
        );

        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                LocalDateTime.parse(value, format);
                return true;
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }
        return false;
    }


    public String trinoProcessing(String sql, String tenantId) {
        JsonElement dataTable = null;
        String queryEndpoint = trinoUri + "/v1/statement";
        final HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(queryEndpoint);
        String encoding = Base64.getEncoder().encodeToString(("admin").getBytes());
        post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
        post.setHeader("content-type", "application/json");
        post.setHeader("X-Trino-Catalog", "hive");
        post.setHeader("X-Trino-Schema", "hive_schema_"+tenantId);
        List<NameValuePair> params = new ArrayList<NameValuePair>();


        post.setEntity(new StringEntity(sql, "UTF-8"));
        //get the response code
        HttpResponse response;
        String jsonString = "";

        try {
            response = client.execute(post);
            jsonString = EntityUtils.toString(response.getEntity());
        } catch (IOException ignored) {

        }
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        JsonObject responseJson = !jsonElement.equals("null") ? jsonElement.getAsJsonObject() : null;
        String newResponseString = "";
        if (responseJson.get("nextUri") != null) {
            JsonObject newResponseJson;
            String newEndpoint = responseJson.get("nextUri").getAsString();

            HttpGet get = new HttpGet(newEndpoint);
            boolean isFirstTime = true;
            do {
                HttpResponse newResponse;
                if (!isFirstTime) {
                    get = new HttpGet(newEndpoint);
                }

                try {
                    newResponse = client.execute(get);
                    newResponseString = EntityUtils.toString(newResponse.getEntity());
                } catch (IOException ignored) {

                }
                JsonElement el = JsonParser.parseString(newResponseString);
                newResponseJson = el.getAsJsonObject();

                if (newResponseJson.get("nextUri") != null) {
                    newEndpoint = newResponseJson.get("nextUri").getAsString();
                }
                if (newResponseJson.get("data") != null) {
                    dataTable = newResponseJson.get("data").getAsJsonArray();
                }
                isFirstTime = false;
            } while (newResponseJson.get("nextUri") != null);
        }
        return newResponseString;
    }

    public boolean createHiveSchema(String tenantId) {
        String sql = "CREATE SCHEMA IF NOT EXISTS hive.hive_schema_"+tenantId+" WITH (location = 's3a://hive-warehouse/')";
        String result = trinoProcessing(sql,tenantId );
        if (result.contains("state\":\"FINISHED")) {
            return true;
        } else {
            return false;
        }
    }
}

