package gr.police.polseal.dto.options;

import java.util.ArrayList;
import java.util.List;

public class ToOptionsSort {

  public List<ToSort> list = new ArrayList<>();

  public ToOptionsSort(String sorts) {
    String[] str = sorts.split(",");
    for (String option : str) {
      String[] spl = option.split(" ");
      this.list.add(new ToSort(spl[0], spl[1]));
    }
  }

}
