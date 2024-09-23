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
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      // Convert byte array to string
      String jsonString = new String(decodedBytes);
      // Convert byte array to JsonNode
      return objectMapper.readTree(jsonString); // Use the string to avoid encoding issues

    } catch (IOException e) {
      System.out.println("Error converting byte array to JSON: " + e.getMessage());
    }
    return null;
  }

  // Converts JsonNode to CSV while retaining header order
  public static String convertJsonToCsv(JsonNode decodedAsJsonNode) throws IOException {
    StringWriter csvWriter = new StringWriter();

    // Get ordered headers (preserving order). Hadn't worked when used Set
    List<String> headers = getHeaders(decodedAsJsonNode);

    // CSVPrinter setup with ordered headers
    try (CSVPrinter csvPrinter = new CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])))) {

      // If the JSON is an array of objects
      if (decodedAsJsonNode.isArray()) {
        for (JsonNode node : decodedAsJsonNode) {
          Map<String, String> flattenedRecord = flattenJson(node, "");
          csvPrinter.printRecord(getValues(flattenedRecord, headers));
        }
      } else {
        // If it's a single object
        Map<String, String> flattenedRecord = flattenJson(decodedAsJsonNode, "");
        csvPrinter.printRecord(getValues(flattenedRecord, headers));
      }
    }
    return csvWriter.toString(); // Return CSV string
  }

  // Flattens the JSON into a Map<String, String> while preserving the order using LinkedHashMap
  private static Map<String, String> flattenJson(JsonNode node, String prefix) {
    Map<String, String> flattenedMap = new LinkedHashMap<>(); // Ordered map

    node.fields().forEachRemaining(entry -> {
      String fieldName = entry.getKey();
      JsonNode childNode = entry.getValue();

      String key = (prefix.isEmpty()) ? fieldName : prefix + "__" + fieldName; // Handle nested keys

      if (childNode.isObject()) {
        flattenedMap.putAll(flattenJson(childNode, key));
      } else if (childNode.isArray()) {
        // Handle arrays by flattening them
        for (int i = 0; i < childNode.size(); i++) {
          flattenedMap.putAll(flattenJson(childNode.get(i), key + "__" + i));
        }
      } else {
        flattenedMap.put(key, childNode.asText());
      }
    });

    return flattenedMap;
  }

  // Extracts the headers in order as a List<String>
  private static List<String> getHeaders(JsonNode jsonNode) {
    Set<String> headers = new LinkedHashSet<>(); // Ordered set for headers
    flattenJson(jsonNode, "").forEach((key, value) -> headers.add(key));
    return new ArrayList<>(headers); // Convert to List
  }

  // Retrieves values in the order of headers
  private static List<String> getValues(Map<String, String> flattenedMap, List<String> headers) {
    List<String> values = new ArrayList<>();
    for (String header : headers) {
      values.add(flattenedMap.getOrDefault(header, "")); // Get value by header or empty string if missing
    }
    return values;
  }
  // Extract headers from the JSON (keys)
//  private static String[] getHeaders(JsonNode jsonNode) {
//    Set<String> headers = new LinkedHashSet<>();
//
//    // Check if the root JSON node contains the "obj" array
//    if (jsonNode.has("obj") && jsonNode.get("obj").isArray()) {
//      // Get the first object from the array to extract the headers
//      JsonNode firstElement = jsonNode.get("obj").get(0);
//      addKeysToSet(firstElement, headers);
//    } else if (jsonNode.isArray()) {
//      // Handle the case where the root itself is an array
//      for (JsonNode node : jsonNode) {
//        addKeysToSet(node, headers);
//        break; // Only get headers from the first object
//      }
//    } else {
//      // If it's a single JSON object
//      addKeysToSet(jsonNode, headers);
//    }
//
//    return headers.toArray(new String[0]); // Convert to array
//  }

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
