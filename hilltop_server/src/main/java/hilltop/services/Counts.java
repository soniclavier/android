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

import hilltop.database.Booking;
import hilltop.database.MysqlDao;
import hilltop.database.Status;

 
@Path("/jwt/counts")
public class Counts {
 
	MysqlDao dao = MysqlDao.getInstance();
	
	@Path("/pending")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public String getPending() {
		List<Booking> bookings = dao.getAllBookings("waiting for approval");
		JsonObject jsonBooking = Json.createObjectBuilder().add("count", bookings.size()).build();
		return jsonBooking.toString();
	}
	
	@Path("/approved")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public String getApproved() {
		List<Booking> bookings = dao.getAllBookings("approved");
		JsonObject jsonBooking = Json.createObjectBuilder().add("count", bookings.size()).build();
		return jsonBooking.toString();
	}
	
	@Path("/rejected")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public String getRejected() {
		List<Booking> bookings = dao.getAllBookings("rejected");
		JsonObject jsonBooking = Json.createObjectBuilder().add("count", bookings.size()).build();
		return jsonBooking.toString();
	}
	
	@Path("/complete")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public String getComplete() {
		List<Booking> bookings = dao.getAllBookings("complete");
		JsonObject jsonBooking = Json.createObjectBuilder().add("count", bookings.size()).build();
		return jsonBooking.toString();
	}
}