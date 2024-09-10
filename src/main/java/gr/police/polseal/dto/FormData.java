package gr.police.polseal.dto;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

public class FormData {

  @FormParam("file")
  @PartType(MediaType.MULTIPART_FORM_DATA)
  private InputStream data;

  @FormParam("name")
  @PartType(MediaType.TEXT_PLAIN)
  private String name;

  @FormParam("type")
  @PartType(MediaType.TEXT_PLAIN)
  private String type;

  @FormParam("size")
  @PartType(MediaType.TEXT_PLAIN)
  private Long size;

  public InputStream getData() {
    return data;
  }

  public void setData(InputStream data) {
    this.data = data;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }
}
