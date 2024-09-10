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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PDFService {


    private static final String TRANSACTION_BUCKET = "transaction-";

    @ConfigProperty(name = "quarkus.sealer.hmac-secret-key")
    private String secretKey;


    private final StorageService storageService;
    private final SigningService signingService;
    private final SealingTemplateService sealingTemplateService;

    public byte[] getSpecificPdf(String rowId, String templateCode) {
        byte[] filedata = null;
        try {
            filedata = storageService.getObjectFromBucket(TRANSACTION_BUCKET + templateCode, rowId + ".pdf");
        } catch (Exception ignored) {
        }
        return filedata;
    }

    public void generateQRCodeImage(String text, int width, int height, String rowId, String templateCode) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath("qr-" + templateCode + "-" + rowId + ".png");
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

    }

    public String generateHmac(String data) {
        try {
            // Define the HMAC algorithm to use (e.g., HmacSHA256, HmacSHA1, HmacSHA512)
            String algorithm = "HmacSHA256";

            // Create a new SecretKeySpec for the specified secret key and algorithm
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), algorithm);

            // Get a Mac instance for the specified algorithm
            Mac mac = Mac.getInstance(algorithm);

            // Initialize the Mac with the given secret key
            mac.init(secretKeySpec);

            // Compute the HMAC on the provided data
            byte[] hmacBytes = mac.doFinal(data.getBytes());

            // Encode the HMAC bytes to a Hex string and return it
            return Hex.encodeHexString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC", e);
        }
    }

    public void addQRCodeToPDFAndSave(String rowId, String templateCode, String qrCodePath, DSSDocument doc, String userid, Long creationTimestamp, SignRequestDto signRequestDto) throws IOException, WriterException, NoSuchAlgorithmException {
//        find the template based on the code given
        SealingTemplate template = sealingTemplateService.findByCode(templateCode);
//        get the position x,y of the qr
        String qrPosition = template.getSignaturesMetadata().get(0).getPosition();

//        fetch the specific pdf from the minio bucket by its rowId and templateCode
//        DSSDocument dssDocument = new InMemoryDocument(getSpecificPdf(rowId, templateCode));
        DSSDocument dssDocument = doc;

        Instant instant = Instant.ofEpochSecond(creationTimestamp);

        LocalDateTime creationDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

//        String hmacDataString = String.format("Author:%s|SerialNumber:%s|CreationDate:%s", signRequestDto.getUserId(), templateCode+"-"+rowId, signRequestDto.getCreationTimestamp());
        String hmacDataString = "Author:"+userid + "|Code:" + templateCode + "-" + rowId + "|Creation:" + creationDateTime;
        String hmac = generateHmac(hmacDataString);
        String finalString = String.join("|", hmac, hmacDataString);


//        generate the qr code that will be used
//        generateQRCodeImage(hmac+"|"+hmacDataString, 100, 100, rowId, templateCode);
        generateQRCodeImage(finalString, 100, 100, rowId, templateCode);

//        load the pdf in order to put the image of the qr code on the FIRST page
        PDDocument document = PDDocument.load(dssDocument.openStream());
        PDPage page = document.getPage(0);
        byte[] imageBytes = Files.readAllBytes(Path.of(qrCodePath));
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, qrCodePath);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);

//      we set maxx and maxy dynamically for every pdf, in order for our seals not to exceed maxx and maxy limits
        float maxx = document.getPage(0).getMediaBox().getWidth();
        float maxy = document.getPage(0).getMediaBox().getHeight();

        //    if width is gt 100px then 100px is set, or else it's shorter
        //    width and height refer to signature
        int width = (int) (maxx / template.getMaxSignatures() > 100 ? 100 : maxx / template.getMaxSignatures());
        int height = 100;

//        we calculate the x,y of the qr on the specific page of the pdf
        int originX = signingService.calculateOriginXByPosition(qrPosition, maxx, width, template.getMaxSignatures(), 0, template.getSignaturesMetadata());
        int originY = signingService.calculateOriginYByPosition(qrPosition, maxy, height);

//        it's needed for the qr to adjust the Y positioning
        float adjustedOriginY = (maxy - originY - height);
        // Adjust the position and size of the QR code
        contentStream.drawImage(pdImage, originX, adjustedOriginY, 100, 100);
        contentStream.close();

//
//
//        List<PDSignatureField> signatureFields = new ArrayList<>();
//
////        if we have to sign every page, it puts virtual invisible signature fields on every page
////        if (template.getSealOnEveryPage()) {
//        // Iterate through each page and add a signature field
//        for (int i = 0; i < document.getNumberOfPages(); i++) {
//            page = document.getPage(i);
//            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
//
//            // If the document does not have an AcroForm, create a new one
//            if (acroForm == null) {
//                acroForm = new PDAcroForm(document);
//                document.getDocumentCatalog().setAcroForm(acroForm);
//            }
//            for (int j=0; j<template.getMaxSignatures(); j++) {
//                // Create a signature field
//                PDSignatureField signatureField = new PDSignatureField(acroForm);
//                signatureField.setPartialName("Signature_" + (j + 1)); // Set a unique name for each signature field
//                // Add the signature field to the acroForm
//                acroForm.getFields().add(signatureField);
//                // Add the signature field to the page
//                page.getAnnotations().add(signatureField.getWidgets().get(0));
//                signatureFields.add(signatureField);
//
//            }
//        }
//        we save the file temporary in order to load it in memory and then in the corresponding minio bucket
        document.save("pdfwithQr-" + template.getCode() + "-" + rowId + ".pdf");
        dssDocument = new InMemoryDocument(new FileInputStream("pdfwithQr-" + template.getCode() + "-" + rowId + ".pdf"));

//        we put the signed pdf with the qr code into the bucket
        storageService.putObjectOnBucket(TRANSACTION_BUCKET + template.getCode(),
                rowId + ".pdf", dssDocument.openStream(), dssDocument.openStream().available(), "application/pdf");
        document.close();

//        deletes the in memory created files that are no more needed
        Files.deleteIfExists(Path.of("pdfwithQr-" + templateCode + "-" + rowId + ".pdf"));
        Files.deleteIfExists(Path.of("qr-" + templateCode + "-" + rowId + ".png"));
        Files.deleteIfExists(Path.of("temporary-" + rowId + ".pdf"));
    }

}

