package gr.police.polseal.dto.page;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

  public List<T> content;
  // Total size of the query. It is not always equal to this.content.size() or this.size
  public Long total;
  // Pagination
  public int index; // pageIndex
  public int size;  //pageSize

  public Page() {
  }

  public Page(List<T> content, int index, int size) {
    this.content = content;
    this.index = index;
    this.size = size;
    this.total = Long.valueOf(Integer.MAX_VALUE);
  }

  public Page(List<T> content, Long total, int index, int size) {
    this.content = content;
    this.total = total;
    this.index = index;
    this.size = size;
  }

  public int getContentSize() {
    return content == null ? 0 : content.size();
  }

  public long getPagesLength() {
    if (total == null || size == 0) {
      return 1;
    }
    return this.total < size ? 1 : (long) Math.ceil((double) total / size);
  }

}
