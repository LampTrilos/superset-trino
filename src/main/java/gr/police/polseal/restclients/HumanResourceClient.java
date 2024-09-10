package gr.police.polseal.restclients;


import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@RegisterRestClient(configKey = "humanresourceclient")
public interface HumanResourceClient {

  @GET
  @Path("/prosopikoAllUsers/energo")
  List<ProsopikoDtoClient> getProsopikoEnergo();

  @GET
  @Path("/mail/allMailsProsopiko")
  List<EmailDtoClient> getProsopikoAllMails();

//  @GET
//  @Path("/vathmos")
//  List<VathmosDtoClient> getVathmos();

  @GET
  @Path("/ypiresia/full")
  List<OuDtoClient> getYpiresia();

}
