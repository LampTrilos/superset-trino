package gr.police.polseal.dto.options;

public class ToSort {

  public String field;
  public String order;

  public ToSort(String field, String order) {
    this.field = field;
    this.order = order;
  }

  @Override
  public String toString() {
    return "ToSort{" + "field=" + field + ", order=" + order + '}';
  }

}
