package gr.police.polseal.dto.options.extensions;


import gr.police.polseal.dto.options.ToList;
import gr.police.polseal.dto.options.ToOptions;

import javax.ws.rs.QueryParam;

public class ToOptionsOu extends ToOptions {

  @QueryParam("id")
  public ToList id;

  @QueryParam("code")
  public String code;

  @QueryParam("name")
  public String name;

  @QueryParam("codeOrName")
  public String codeOrName;

}
