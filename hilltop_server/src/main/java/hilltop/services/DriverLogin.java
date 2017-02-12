package hilltop.services;
 
 
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
 
@Path("/driverLogin")
public class DriverLogin {

	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String driverLogin(String userpass) {
		JsonReader reader = Json.createReader(new StringReader(userpass));
		JsonObject loginObject = reader.readObject();
		reader.close();
		String username = loginObject.getString("user");
		String pass = loginObject.getString("pass");
		JsonObject result =  Json.createObjectBuilder().add("status","failed").build();
		
		if (username.equals("driver") && pass.equals("hilltop"))
			result =  (JsonObject) Json.createObjectBuilder().add("status","success").build();
		
		return result.toString();
	}
}