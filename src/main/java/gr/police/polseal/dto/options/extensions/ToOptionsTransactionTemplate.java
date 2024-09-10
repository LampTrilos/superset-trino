package gr.police.polseal.dto.options.extensions;


import gr.police.polseal.dto.options.ToList;
import gr.police.polseal.dto.options.ToOptions;

import javax.ws.rs.QueryParam;

public class ToOptionsTransactionTemplate extends ToOptions {

  @QueryParam("id")
  public ToList id;

  @QueryParam("description")
  public String description;

  public Long user;

}
