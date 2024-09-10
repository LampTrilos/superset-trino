package gr.police.polseal.service;

/**
 *
 * @author pgouvas
 */
public enum TransactionError {
  ACCOUNT_MUST_BE_DISCREET("Ο κωδικός πρέπει να είναι διακριτός"),
  ACCOUNT_MUST_BE_GREATER_THAN_ZERO("Η ποσότητα πρέπει να είναι μεγαλύτερη του μηδενός"),
  ACCOUNT_MUST_NOT_BE_LESS_THAN_ZERO("Η ποσότητα δεν πρέπει να είναι μικρότερη του μηδενός"),
  ACCOUNT_SOURCE_NOT_EXISTING("Ο Λογαριασμός Προέλευσης δεν υπάρχει"),
  ACCOUNT_TARGET_NOT_EXISTING("Ο Λογαριασμός Προορισμού δεν υπάρχει"),
  AMOUNT_OF_ITEMS_NOT_AVAILABLE("Η Ποσότητα των Ειδών δεν είναι Διαθέσιμη. Κωδικός:"),
  AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE("Η Ποσότητα των Ειδών ξεπερνάει το γενικό σύνολο. Κωδικός:"),
  AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE_FROM_USER("Η Ποσότητα των Ειδών ξεπερνάει το γενικό σύνολο του χρήστη. Κωδικός:"),
  AMOUNT_OF_ITEMS_EXCEEDS_TOTAL_BALANCE_FROM_DEP_UNIT("Η Ποσότητα των Ειδών ξεπερνάει το γενικό σύνολο που έχετε λάβει από τη διαχείριση. Κωδικός:");

  public final String label;

  private TransactionError(String label) {
    this.label = label;
  }
}
