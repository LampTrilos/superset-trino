package gr.police.polseal.service;

import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.enumerations.TextWrapping;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.pades.*;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.pades.validation.PDFDocumentValidator;
import eu.europa.esig.dss.pdf.pdfbox.PdfBoxNativeObjectFactory;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.ubitech.bitt.core.domain.storage.service.StorageService;
import gr.police.polseal.model.SealingTemplate;
import gr.police.polseal.model.SignatureMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class SigningService {

    private final StorageService storageService;

    @ConfigProperty(name = "certificate.path", defaultValue = "C:\\Developing\\intellij-projects\\ubi\\pol-seal\\certificate.p12")
    String certificatepath;

    @ConfigProperty(name = "certificate.password", defaultValue = "1q2w3e")
    String password;

    private static final String TRANSACTION_BUCKET = "transaction-";


    @Transactional
    @Traced
    public DSSDocument signPdfDocument(SealingTemplate template, DSSDocument dssDocument, String signaturetext, int sealIndex, int totalPages, String rowId) throws IOException {

//        load document
        DSSDocument toSignDocument = dssDocument;
        PDDocument document = PDDocument.load(toSignDocument.openStream());

//        PDDocument document = doc;

        //List<PDSignatureField> signatureFields = new ArrayList<>();
        //The global Acroform for the document
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        // If the document does not have an AcroForm, create a new one
        if (acroForm == null) {
            acroForm = new PDAcroForm(document);
            document.getDocumentCatalog().setAcroForm(acroForm);
        }

//        if we have to sign every page, it puts virtual invisible signature fields on every page
        if (template.getSealOnEveryPage()) {
            // Iterate through each page and add a signature field to all pages if the template warrants it, or just the last page if not
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                PDPage page = document.getPage(i);
                // Create a signature field
                PDSignatureField signatureField = new PDSignatureField(acroForm);
                signatureField.setPartialName("Signature_" + (i + 1)); // Set a unique name for each signature field
                // Add the signature field to the AcroForm or signature invalidates previous signatures
                acroForm.getFields().add(signatureField);
                // Add the signature field to the page
                page.getAnnotations().add(signatureField.getWidgets().get(0));
                //signatureFields.add(signatureField);
            }
        }
        // or just the last page if not
        else {
            PDPage page = document.getPage(document.getNumberOfPages() - 1);
            // Create a signature field
            PDSignatureField signatureField = new PDSignatureField(acroForm);
            signatureField.setPartialName("Signature_" + (document.getNumberOfPages() - 1)); // Set a unique name for each signature field
            // Add the signature field to the AcroForm or signature invalidates previous signatures
            acroForm.getFields().add(signatureField);
            // Add the signature field to the page
            page.getAnnotations().add(signatureField.getWidgets().get(0));
        }

        // Save the modified document
        document.save(new File("temporary-" + rowId + ".pdf"));
        document.close();

        toSignDocument = new InMemoryDocument(new FileInputStream("temporary-" + rowId + ".pdf"));
        document = PDDocument.load(toSignDocument.openStream());


        Pkcs12SignatureToken signingToken = new Pkcs12SignatureToken(certificatepath, new KeyStore.PasswordProtection(password.toCharArray()));
        DSSPrivateKeyEntry privateKey = signingToken.getKeys().get(0);


        SignatureImageParameters imageParameters = new SignatureImageParameters();
        // initialize signature field parameters
        SignatureFieldParameters fieldParameters = new SignatureFieldParameters();
        imageParameters.setFieldParameters(fieldParameters);

        //we set maxx and maxy dynamically for every pdf, in order for our seals not to exceed maxx and maxy limits
        float maxx = document.getPage(0).getMediaBox().getWidth();
        float maxy = document.getPage(0).getMediaBox().getHeight();

//    if width is gt 100px then 100px is set, or else it's shorter
//    width and height refer to signature
        int width = (int) (maxx / template.getMaxSignatures() > 100 ? 100 : maxx / template.getMaxSignatures());
        int height = 100;

//       MATSAKONIA! we get to the new list only the true signatures, leaving the index=0 which is the QR Code
        List<SignatureMetadata> metadata = template.getSignaturesMetadata().subList(1, template.getSignaturesMetadata().size());

        // Calculate the x and y origin
//    it refers to each and every seal (if there are more than 1)
//        BEWARE sealIndex = 0 refers to QR code
        int originX = 0;
        int originY = 0;

        if (sealIndex < metadata.size()) {
//            we calculate the specific x,y
            originX = calculateOriginXByPosition(metadata.get(sealIndex).getPosition(), maxx, width, template.getMaxSignatures(), sealIndex, metadata);
            originY = calculateOriginYByPosition(metadata.get(sealIndex).getPosition(), maxy, height);
        }

        fieldParameters.setOriginX(originX);
        fieldParameters.setOriginY(originY);
        fieldParameters.setWidth(width);
        fieldParameters.setHeight(height);


        // tag::font[]
        // Initialize text to generate for visual signature
        DSSFont font = new DSSFileFont(getClass().getResourceAsStream("/fonts/Arial.ttf"));
        font.setSize(12);
        // end::font[]
        // tag::text[]
        // Instantiates a SignatureImageTextParameters object
        SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
        // Allows you to set a DSSFont object that defines the text style (see more information in the section "Fonts usage")
        textParameters.setFont(font);
        // Defines the text content
        textParameters.setText(signaturetext);
        // Defines the color of the characters
        textParameters.setTextColor(Color.BLACK);
        // Defines the background color for the area filled out by the text
//        textParameters.setBackgroundColor(new Color(0,0,0,0));  //transparent
        textParameters.setBackgroundColor(Color.white);
        // Defines a padding between the text and a border of its bounding area
        textParameters.setPadding(20);

        // TextWrapping parameter allows defining the text wrapping behavior within  the signature field
    /*
        FONT_BASED - the default text wrapping, the text is computed based on the given font size;
        FILL_BOX - finds optimal font size to wrap the text to a signature field box;
        FILL_BOX_AND_LINEBREAK - breaks the words to multiple lines in order to find the biggest possible font size to wrap the text into a signature field box.
     */
        textParameters.setTextWrapping(TextWrapping.FILL_BOX_AND_LINEBREAK);
        // Set textParameters to a SignatureImageParameters object

        imageParameters.setTextParameters(textParameters);
        PAdESSignatureParameters parameters = new PAdESSignatureParameters();
        // We set the signing certificate
        parameters.setSigningCertificate(privateKey.getCertificate());
        // We set the certificate chain
        parameters.setCertificateChain(privateKey.getCertificateChain());
        parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
        parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);

        parameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);
        parameters.bLevel().setSigningDate(new Date());


        imageParameters.setTextParameters(textParameters);
        imageParameters.setFieldParameters(fieldParameters);
        parameters.setImageParameters(imageParameters);


        // Create common certificate verifier
        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
        // Create PAdESService for signature
        PAdESService service = new PAdESService(commonCertificateVerifier);
        // tag::custom-factory[]
        service.setPdfObjFactory(new PdfBoxNativeObjectFactory());
        // end::custom-factory[]


        float fontSize = 12;
        float fieldWidth = width;  // Width of the signature field
        float fieldHeight = height;  // Height of the signature field
        float leading = 1.2f * fontSize;  // Line spacing

        DSSDocument finalSignedDocument = null;
        if (template.getSealOnEveryPage()) {
            // Load custom font (Arial) from resources
            InputStream fontStream = getClass().getResourceAsStream("/fonts/Arial.ttf");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PDType0Font fontPD = PDType0Font.load(document, fontStream);
            for (int pageCount = 0; pageCount < document.getPages().getCount(); pageCount++) {

                if (pageCount != totalPages - 1) {
                    PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(pageCount), PDPageContentStream.AppendMode.APPEND, true);

                    // Calculate text width and height
                    float textWidth = fontPD.getStringWidth(textParameters.getText()) / 1000 * fontSize;
                    float textHeight = fontSize;

                    // Set origin for the box
                    float adjustedOriginX = fieldParameters.getOriginX(); //+ textParameters.getPadding();
//                    float adjustedOriginY = (maxy - fieldParameters.getOriginY() );
                    float adjustedOriginY = (maxy - fieldParameters.getOriginY() - height);

                    // Set background to transparent
//                    contentStream.setNonStrokingColor(new Color(0,0,0,0));
                    contentStream.setNonStrokingColor(Color.white);
                    contentStream.addRect(adjustedOriginX, adjustedOriginY, fieldWidth, fieldHeight);
                    contentStream.fill();

                    // Set text color to black
                    contentStream.setNonStrokingColor(Color.BLACK);

//                    manipulation of the text of the signature
                    contentStream.beginText();
                    contentStream.setFont(fontPD, fontSize);
                    contentStream.newLineAtOffset(adjustedOriginX, (float) adjustedOriginY + ((float) height / 2));// Adjust the position as needed

                    // Wrap text within the signature field
                    String[] words = textParameters.getText().split(" ");
                    StringBuilder line = new StringBuilder();

                    for (String word : words) {
                        if (fontPD.getStringWidth(line + word) / 1000 * fontSize > fieldParameters.getWidth()) {
                            contentStream.showText(line.toString());
                            contentStream.newLineAtOffset(0, -leading);
                            line = new StringBuilder();
                        }
                        line.append(word).append(" ");
                    }
                    // Show the last line
                    contentStream.showText(line.toString().trim());
                    contentStream.endText();
                    contentStream.close();
                }
            }
            // Save the updated PDF
            document.save(out);
            // Create a DSSDocument from the updated PDF
            finalSignedDocument = new InMemoryDocument(out.toByteArray());
            document.close();
        } else {
//            finalSignedDocument = newDss;
            finalSignedDocument = toSignDocument;
        }
        fieldParameters.setPage(document.getNumberOfPages());
        imageParameters.setFieldParameters(fieldParameters);
        parameters.setImageParameters(imageParameters);

//      Cryptographic signing
        ToBeSigned dataToSign = service.getDataToSign(finalSignedDocument, parameters);
        DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();
        SignatureValue signatureValue = signingToken.sign(dataToSign, digestAlgorithm, privateKey);
        DSSDocument signedDocument = service.signDocument(finalSignedDocument, parameters, signatureValue);

        signedDocument.save("signed_" + rowId + ".pdf");
        document.close();
        Files.deleteIfExists(Path.of("temporary-" + rowId + ".pdf"));

        storageService.putObjectOnBucket(TRANSACTION_BUCKET + template.getCode(), rowId + ".pdf", signedDocument.openStream(), signedDocument.openStream().available(), "application/pdf");
        return signedDocument;
    }

    public int getDocumentPagesCount(DSSDocument doc) throws IOException {
        PDDocument document = PDDocument.load(doc.openStream());
        int totalPages = document.getNumberOfPages();
        document.close();
        return totalPages;
    }

    public DSSDocument checkIfExistsAlreadyAndFetch(String templateCode, String rowId) throws FileNotFoundException {
        byte[] filedata = null;
        try {
            filedata = storageService.getObjectFromBucket(TRANSACTION_BUCKET + templateCode, rowId + ".pdf");
        } catch (Exception ignored) {
        }
        DSSDocument document = null;
//        if file exists in minio bucket it is being fetched, else the temporary pdf file is being used
        if (filedata != null) {
            document = new InMemoryDocument(filedata);
        } else {
            document = new InMemoryDocument(new FileInputStream("temporary-" + rowId + ".pdf"));
        }
        return document;
    }

    public int getPDFSignaturesSofar(DSSDocument document) {

//      Initialize the PDF document validator
        PDFDocumentValidator pdfValidator = new PDFDocumentValidator(document);
        pdfValidator.setCertificateVerifier(new CommonCertificateVerifier());

        // Generate the validation reports
        Reports reports = pdfValidator.validateDocument();

        int signaturesCount = 0;

        if (reports.getDiagnosticData().getSignatures() != null) {
            signaturesCount = reports.getDiagnosticData().getSignatures().size();
        }
        return signaturesCount;

    }

    public boolean checkSignatureValidity(DSSDocument document) {

//      Initialize the PDF document validator
        PDFDocumentValidator pdfValidator = new PDFDocumentValidator(document);
        pdfValidator.setCertificateVerifier(new CommonCertificateVerifier());

        // Generate the validation reports
        Reports reports = pdfValidator.validateDocument();
        List<SignatureWrapper> signatures = reports.getDiagnosticData().getSignatures();

        // We check if there are any signatures and validate the last one
        if (!signatures.isEmpty()) {
            //for (SignatureWrapper sig : signatures) {
                if (!signatures.get(signatures.size() - 1).isSignatureValid()) {
                    return false;
                }
            //}
//            SignatureWrapper lastSignature = signatures.get(signatures.size() - 1);
//            if (!lastSignature.isSignatureValid()) {
//                return false;
//            }
        }
        return true;
    }

    //Calculates the position for each signatured so they all align next to each other
    public int calculateOriginXByPosition(String position, float maxx, int width, int maxSignatures, int sealIndex, List<SignatureMetadata> signaturesMetadata) {
        int originX = 0;
        int padding = 15;
        //the index for the specific y of the current signature (Meaning it might be sign. No. 3 but the first on medium level)
        //So we see how many signatures before our current index can be found in the same y, but before our own global index
        int indexInCurrentY = signaturesMetadata.subList(0, sealIndex).stream().filter(metadata -> metadata.getPosition().charAt(0) == position.charAt(0)).collect(Collectors.toList()).size();

//       left
        if (position.equalsIgnoreCase("00") || position.equalsIgnoreCase("10") || position.equalsIgnoreCase("20")) {
//            originX = sealIndex * width;
            originX = indexInCurrentY * width;
//            originX = padding;
        }
//       Center
        else if (position.equalsIgnoreCase("01") || position.equalsIgnoreCase("11") || position.equalsIgnoreCase("21")) {
//            originX = (int) ((maxx - (((maxSignatures - 1) * padding) + (maxSignatures * width))) / 2)  + sealIndex * width;
            originX = (int) ((maxx - (((maxSignatures - 1) * padding) + (maxSignatures * width))) / 2)  + indexInCurrentY * width;
//            originX = (int) ((maxx - (((maxSignatures - 1) * padding) + (maxSignatures * width))) / 2) ;
//            originX = (int) ((maxx - width - padding) / 2);
        }
//        Right
        else if (position.equalsIgnoreCase("02") || position.equalsIgnoreCase("12") || position.equalsIgnoreCase("22")) {
//            originX = (int) (maxx - (maxSignatures * padding) - (maxSignatures * width)) + (sealIndex * width);
            originX = (int) (maxx - (maxSignatures * padding) - (maxSignatures * width)) + (indexInCurrentY * width);
//            originX = (int) (maxx - width - padding);
        }
        return originX;
    }

    public int calculateOriginYByPosition(String position, float maxy, int height) {
        int originY = 0;
        int padding = 15;

        // Bottom
        if (position.equalsIgnoreCase("00") || position.equalsIgnoreCase("01") || position.equalsIgnoreCase("02")) {
            originY = (int) (maxy - height);
        }
//        Middle
        else if (position.equalsIgnoreCase("10") || (position.equalsIgnoreCase("11")) || (position.equalsIgnoreCase("12"))) {
            originY = (int) (maxy / 2);
        }
//        Top
        else if (position.equalsIgnoreCase("20") || (position.equalsIgnoreCase("21")) || (position.equalsIgnoreCase("22"))) {
            originY = padding;
        }
        return originY;
    }

}
