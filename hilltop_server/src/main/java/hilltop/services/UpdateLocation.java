package hilltop.services;
 
 
 
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.Json;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

 
@Path("/updateLocation")
public class UpdateLocation {
 
	@Path("{location}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public String convertCtoFfromInput(@PathParam("location") String location) {
		Double lat;
		Double lon;
		Float bearing;
		String[] parts = location.split(",");
		lat = Double.parseDouble(parts[0]);
		lon = Double.parseDouble(parts[1]);
		bearing = Float.parseFloat(parts[2]);
		JsonObject result =  Json.createObjectBuilder().add("status","failed to sent location").build(); 
		if (GcmSender.sendMessage(location))
			result =  Json.createObjectBuilder().add("status","location sent("+lat+","+lon+") : bearing "+bearing).build();
		return result.toString();
			
	}
}