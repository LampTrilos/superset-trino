package gr.police.polseal.service.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.police.polseal.exception.BadRequestAlertException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.microprofile.opentracing.Traced;


import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


public class GeneralUtils {

  // TODO IMPORTANT change if change codes min/max
  private static final int CODE_LENGTH = 7;

  @Traced
  private static String transformStringWithLeadingZeros(String str) {
    // Remove leading zeros from the original string
    String strippedStr = str.replaceFirst("^0+(?!$)", "");

    // Calculate the number of zeros to be added
    int numZerosToAdd = CODE_LENGTH - strippedStr.length();

    // Construct the transformed string with leading zeros
    String transformedStr = String.format("%0" + numZerosToAdd + "d%s", 0, strippedStr);

    return transformedStr;
  }

  @Traced
  public static String transformCodeWithLeadingZeros(String code) {
    if (code == null || code.isEmpty()) {
      return "";
    }
    String[] parts = code.split(" ");
    StringBuilder formattedString = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      String part = parts[i];
      String formattedPart = transformStringWithLeadingZeros(part.trim());
      formattedString.append(" ").append(formattedPart);
    }
    return formattedString.toString().trim();
  }

  // Recursive method to traverse the exception chain and find PSQLException
  public static BadRequestAlertException traverseAndFindPSQLException(Throwable throwable) {
    if (throwable == null) {
      return null;
    } else if (throwable instanceof SQLException && throwable.getClass().getName().equals("org.postgresql.util.PSQLException")) {
      if (throwable.getMessage().contains("Cannot change inTransactionId from a non-NULL value to another non-NULL value")) {
        return new BadRequestAlertException("Το διακριτό υλικό περιέχεται ήδη σε συναλλαγή Προς Υπογραφή");
      } else if (throwable.getMessage().contains("Item count exceeds the balance in accountbalance")) {
        return new BadRequestAlertException("Έχετε ήδη απογράψει όλα τα υλικά ");
      } else {
        return new BadRequestAlertException(throwable.getMessage());
      }
    } else {
      return traverseAndFindPSQLException(throwable.getCause());
    }
  }

  @Traced
  public static LocalDate xmlGregorianCalendarToLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
    if (xmlGregorianCalendar == null) {
      return null;
    }
    LocalDate localDate = LocalDate.of(
        xmlGregorianCalendar.getYear(),
        xmlGregorianCalendar.getMonth(),
        xmlGregorianCalendar.getDay());
    return localDate;
  }

  public static String extractItemCodeForExternalSystems(String input) {
    // Split the input string by spaces
    String[] parts = input.split("\\s+");
    if (parts.length >= 2) {
      String firstPart = parts[0]; // "0000046"
      String secondPart = parts[1]; // "0000113"
      // Extract last 2 characters from firstPart
      String extractedFirstPart = firstPart.substring(firstPart.length() - 2);
      // Extract last 3 characters from secondPart
      String extractedSecondPart = secondPart.substring(secondPart.length() - 3);
      return extractedFirstPart + extractedSecondPart;
    } else {
      System.out.println("Invalid input format");
      return null;
    }
  }

  public static String[] extractingCSVHeaders(String fileName){
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      // Read the first line which is the header
      String headerLine = br.readLine().replace("\"","");

      if (headerLine != null) {
        // Split the header by commas
        String[] header = headerLine.split(",");
        return header;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static JsonNode convertByteToJsonString(byte[] decodedBytes) {
    // Convert the byte array to JSON
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      // Convert byte array to JsonNode

        return objectMapper.readTree(decodedBytes);

    } catch (IOException e) {
      System.out.println("Error converting byte array to JSON: " + e.getMessage());
    }
    return null;
  }

  public static String convertJsonToCsv(JsonNode decodedAsJsonNode) {
    StringWriter csvWriter = new StringWriter();

    // CSVPrinter setup
    try (CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader(getHeaders(decodedAsJsonNode)))) {

      // If the JSON is an array of objects
      if (decodedAsJsonNode.has("obj") && decodedAsJsonNode.get("obj").isArray()) {
        // Get the array from the "obj" field
        JsonNode objArray = decodedAsJsonNode.get("obj");
        // Process each object in the array
        for (JsonNode node : objArray) {
          csvPrinter.printRecord(getValues(node));
        }
      } else if (decodedAsJsonNode.isArray()) {
        // If the root itself is an array (unlikely in your case but handled for generality)
        for (JsonNode node : decodedAsJsonNode) {
          csvPrinter.printRecord(getValues(node));
        }
      } else {
        // If it's a single object
        csvPrinter.printRecord(getValues(decodedAsJsonNode));
      }
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

      return csvWriter.toString(); // Return CSV string
  }
  // Extract headers from the JSON (keys)
  private static String[] getHeaders(JsonNode jsonNode) {
    Set<String> headers = new LinkedHashSet<>();

    // Check if the root JSON node contains the "obj" array
    if (jsonNode.has("obj") && jsonNode.get("obj").isArray()) {
      // Get the first object from the array to extract the headers
      JsonNode firstElement = jsonNode.get("obj").get(0);
      addKeysToSet(firstElement, headers);
    } else if (jsonNode.isArray()) {
      // Handle the case where the root itself is an array
      for (JsonNode node : jsonNode) {
        addKeysToSet(node, headers);
        break; // Only get headers from the first object
      }
    } else {
      // If it's a single JSON object
      addKeysToSet(jsonNode, headers);
    }

    return headers.toArray(new String[0]); // Convert to array
  }

  // Extract values from the JSON object for a CSV row
  private static Object[] getValues(JsonNode jsonNode) {
    List<Object> values = new ArrayList<>();
    Iterator<String> fieldNames = jsonNode.fieldNames();

    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      JsonNode valueNode = jsonNode.get(fieldName);
      values.add(valueNode != null ? valueNode.asText() : "");
    }

    return values.toArray(); // Convert to array for CSVPrinter
  }

  // Helper method to extract keys and add them to a set
  private static void addKeysToSet(JsonNode jsonNode, Set<String> headers) {
    Iterator<String> fieldNames = jsonNode.fieldNames();
    while (fieldNames.hasNext()) {
      headers.add(fieldNames.next());
    }
  }
}
