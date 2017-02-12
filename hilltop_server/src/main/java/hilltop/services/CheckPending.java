package hilltop.services;
 
 
 
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.Json;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;

import hilltop.database.MysqlDao;
import hilltop.database.Status;

 
@Path("/checkPending")
public class CheckPending {
 
	MysqlDao dao = MysqlDao.getInstance();
	
	@Path("{buid}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	
	public String checkPendingStatus(@PathParam("buid") String id) {
		int buid = Integer.parseInt(id);
		List<Status> bookings = dao.getPendingBookings(buid);
		String pending = "false";
		if (bookings.size()>0)
			pending="true";
		JsonObject jsonBooking = Json.createObjectBuilder().add("pending",pending).build();
		return jsonBooking.toString();
			
	}
}