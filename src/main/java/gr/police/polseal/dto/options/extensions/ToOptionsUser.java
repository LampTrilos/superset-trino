package gr.police.polseal.dto.options.extensions;


import gr.police.polseal.dto.options.ToList;
import gr.police.polseal.dto.options.ToOptions;

import javax.ws.rs.QueryParam;
import java.time.LocalDate;

public class ToOptionsUser extends ToOptions {

  @QueryParam("id")
  public ToList id;

  @QueryParam("lastName")
  public String lastName;

  @QueryParam("firstName")
  public String firstName;

  @QueryParam("gender")
  public ToList gender;

  @QueryParam("birthDateFrom")
  public LocalDate birthDateFrom;

  @QueryParam("birthDateTo")
  public LocalDate birthDateTo;

  @QueryParam("name")
  public String name;

  @QueryParam("role")
  public String role;

  @QueryParam("ouId")
  public String ouId;

  @QueryParam("depUnitId")
  public String depUnitId;

}
