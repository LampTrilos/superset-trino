package gr.police.polseal.dto.options;

import java.util.ArrayList;
import java.util.List;

public class ToList {
  public List<String> list = new ArrayList<>();

  public ToList(String query) {
    String[] items = query.split(",");
    for (String item : items) {
      if (!item.isBlank()) {
        this.list.add(item);
      }
    }
  }

  @Override
  public String toString() {
    return "ToList{" + "list=" + list + '}';
  }

}
