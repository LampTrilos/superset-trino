package gr.police.polseal.dto.page;

import java.io.Serializable;
import java.util.List;

public class PageList<T> implements Serializable {

  public List<T> content;

  public PageList(List<T> content) {
    this.content = content;
  }

  public int getContentSize() {
    return content == null ? 0 : content.size();
  }

}
