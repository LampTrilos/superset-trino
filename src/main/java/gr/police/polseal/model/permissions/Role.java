package gr.police.polseal.model.permissions;

public enum Role {
  SUPER_ADMIN("Υπερδιαχειριστής"),
  DIACHEIRISTIS_KODIKOLOGIOU("Διαχειριστής Κωδικολογίου"),
  DIACHEIRISTIS_KATHIKONTOLIGIOU("Διαχειριστής Καθηκοντολογίου"),
  GENIKOS_DIACHEIRISTIS_YLIKOU("Γενικός Διαχειριστής Υλικού"),
  MERIKOS_DIACHEIRISTIS_YLIKOU("Μερικός Διαχειριστής Υλικού"),
  TMIMATARCHIS("Επόπτης Οικονομικών Υπηρεσιών"),
  DIEFTHYNTIS("Διευθυντής Υπηρεσίας"),
  ANAPLIROTIS_DIEFTHYNTIS("Αναπληρωτής Διευθυντής Υπηρεσίας"),
  SYNTAKTIS_DAPANON_PROMITHEIAS("Συντάκτης Δαπανών/Προμήθειας"),
  TOPIKOS_YPEYTHYNOS("Τοπικός Υπεύθυνος Υπηρεσίας");

  private final String description;

  Role(String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }

}