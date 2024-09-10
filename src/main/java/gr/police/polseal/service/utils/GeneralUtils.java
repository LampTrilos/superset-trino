package gr.police.polseal.service.utils;

import gr.police.polseal.exception.BadRequestAlertException;
import org.eclipse.microprofile.opentracing.Traced;

import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.SQLException;
import java.time.LocalDate;

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

}
