package gr.police.polseal.dto.options;


import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;

//Who is trying to sign what basically
public class SignRequestParams {

  @DefaultValue("262654")
  @HeaderParam("IV-USER")
  public String arithmosMitroou;

  @DefaultValue("262654")
  @HeaderParam("IV-USER-l")
  public String ivuserl;

  @DefaultValue("#$056YPOX#:a_user#:0#:0#:1#:2970#:#$056YPOX#:s_u_all#:0#:0#:1#:15786#:")
  @HeaderParam("ROLOS-1")
  public String rolos;

  @DefaultValue("275771")
  @HeaderParam("YPIRESIA-1")
  public String ypiresia;

  @DefaultValue("127.0.0.1")
  @HeaderParam("IV-REMOTE-ADDRESS")
  public String ivremoteaddress;


  //The template that corresponds to what the user is trying to save
  @DefaultValue("")
  @QueryParam("templateCode")
  public String templateCode;

  //the id of the row of the 3d party app, for logging purposes
  @DefaultValue("")
  @QueryParam("rowId")
  public String rowId;

}
