package gr.police.polseal.dto.options.extensions;


import gr.police.polseal.dto.options.ToOptions;

import javax.ws.rs.QueryParam;

public class ToOptionsNotification extends ToOptions {

  @QueryParam("read")
  public Boolean read;

  public Long userId;

}
