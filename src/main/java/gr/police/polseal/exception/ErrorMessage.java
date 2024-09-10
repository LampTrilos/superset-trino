package gr.police.polseal.exception;

public enum ErrorMessage {

  VERIFICATION_CODE_ERROR("Ο κωδικός επαλήθευσης δεν είναι σωστός");

  private final String description;

  ErrorMessage(String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }

}
