package gr.police.polseal.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.ubitech.bitt.core.domain.storage.service.StorageService;
import gr.police.polseal.dto.SignRequestDto;
import gr.police.polseal.model.SealingTemplate;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;


@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class TransformLoadDataService {


    private static final String RAW_BUCKET = "raw-data";


    private final StorageService storageService;
    @ConfigProperty(name = "minio.endpoint")
    private String minioendpoint;

    @ConfigProperty(name = "minio_accesskey")
    private String minioaccesskey;

    @ConfigProperty(name = "minio_secretkey")
    private String miniosecretkey;


    public void loadFileTOBucket(String csvAsBase64, String fileName, String tenantId) throws Exception {
        // Decode the Base64 string to byte array
        byte[] decodedBytes = Base64.getDecoder().decode(csvAsBase64);
        // Create a temporary CSV file
        String tempName = "temporary_"+tenantId+"_"+fileName;
        File csvFile = new File(tempName+".csv");
        FileUtils.writeByteArrayToFile(csvFile, decodedBytes);

        putObjectOnBucket(tenantId, RAW_BUCKET ,
                tempName, "csv");

//        storageService.putObjectOnBucket(RAW_BUCKET,
//                tenantId+"/"+fileName + ".csv", FileUtils.openInputStream(csvFile),csvFile.length(), "text/csv");
        Files.deleteIfExists(Path.of(tempName+".csv"));
    }

//    public byte[] getSpecificPdf(String rowId, String templateCode) {
//        byte[] filedata = null;
//        try {
//            filedata = storageService.getObjectFromBucket(TRANSACTION_BUCKET + templateCode, rowId + ".pdf");
//        } catch (Exception ignored) {
//        }
//        return filedata;
//    }


    public void putObjectOnBucket(String tenantiId, String bucketname, String objectname, String mimetype)
            throws Exception {

        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioendpoint)
                .credentials(minioaccesskey, miniosecretkey)
                .build();

        boolean foundRawData = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketname).build());
        if (!foundRawData) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketname).build());
            System.out.println("Bucket "+bucketname+" created ");
            minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket(bucketname)
                    .object(tenantiId+"/"+objectname + "." + mimetype)
                    .filename(objectname + "." + mimetype)
                    .build());
            System.out.println(" " + objectname + "." + mimetype + " file uploaded to bucket " + bucketname);
        } else {
            minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket(bucketname)
                    .object(objectname + "." + mimetype)
                    .filename(objectname + "." + mimetype)
                    .build());
            System.out.println(" " + objectname + "." + mimetype + " file uploaded to bucket " + bucketname);
        }
    }

}

