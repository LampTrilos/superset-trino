package gr.police.polseal.service;

public enum JwtClaim {
  USER_ID("userid"),
  DEP_UNIT_ID("depunitid");

  private final String description;

  public String getDescription() {
    return this.description;
  }

  private JwtClaim(final String description) {
    this.description = description;
  }
}