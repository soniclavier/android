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

 
@Path("/markTripComplete")
public class MarkTripComplete {
 
	MysqlDao dao = MysqlDao.getInstance();
	
	@Path("{buid}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	
	public String markTripComplete(@PathParam("buid") String id) {
		int buid = Integer.parseInt(id);
		List<Status> bookings = dao.getPendingBookings(buid);
		if (bookings.size() == 0) {
			JsonObject jsonBooking = Json.createObjectBuilder().add("status","no pending rides").build();
			return jsonBooking.toString();
		}
		Status oldest = bookings.get(bookings.size()-1);
		dao.changeStatus(oldest.id,"complete");
		JsonObject jsonBooking = Json.createObjectBuilder().add("status",oldest.id+" marked as complete").build();
		return jsonBooking.toString();
			
	}
}