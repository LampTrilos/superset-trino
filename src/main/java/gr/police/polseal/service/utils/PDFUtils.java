package gr.police.polseal.service.utils;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.eclipse.microprofile.opentracing.Traced;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class PDFUtils {

  @Traced
  public static File generatePDFFromHTML(String filename, String renderedtemplateinstance) throws IOException {
    File file = File.createTempFile(filename, ".pdf");
    try (OutputStream os = new FileOutputStream(file)) {
      PdfRendererBuilder builder = new PdfRendererBuilder();
      builder.useFastMode();
      builder.useFont(() -> Thread.currentThread().getContextClassLoader().getResourceAsStream("fonts/Arial.ttf"), "Arial");
      builder.withHtmlContent(renderedtemplateinstance, "");
      builder.toStream(os);
      builder.run();
      return file;
    }
  } //EoM
 
  public static String getTimestamp() {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Athens"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    String formatednow = now.format(formatter);
    return formatednow;
  }

  // Utility method to convert Base64 string to InputStream
  public static InputStream base64ToInputStream(String base64String) {
    byte[] decodedBytes = Base64.getDecoder().decode(base64String);
    return new ByteArrayInputStream(decodedBytes);
  }

  // Utility method to decode Base64 string and save it as a PDF file
  public static void decodeBase64ToPdf(String base64String, String filePath) throws IOException {
    // Decode Base64 string to byte array
    byte[] decodedBytes = Base64.getDecoder().decode(base64String);

    // Write the byte array to a file
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
      fos.write(decodedBytes);
    }
  }
  
}
