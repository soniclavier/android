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

 
@Path("/jwt/list")
public class GetList {
 
	MysqlDao dao = MysqlDao.getInstance();
	
	@GET
	@Path("/approved")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllApproved() {
		List<Booking> bookings = dao.getAllBookings("approved");
		JsonArrayBuilder list = Json.createArrayBuilder();
		for(Booking booking : bookings) {
			System.out.println(booking.toString());
			JsonObject jsonBooking = Json.createObjectBuilder()
					.add("id", booking.getId())
					.add("datetime",booking.getDate().toString())
					.add("name",booking.getName())
					.add("buid",booking.getBuid())
					.add("num_students",booking.getNumStuds())
					.add("destination",booking.getDest())
					.add("source",booking.getSource())
					.add("phone",booking.getPhone())
					.add("status",booking.getStatus())
					.build();
			list.add(jsonBooking);
		}
		
		return list.build().toString();
			
	}
	
	@GET
	@Path("/rejected")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllRejected() {
		List<Booking> bookings = dao.getAllBookings("rejected");
		JsonArrayBuilder list = Json.createArrayBuilder();
		for(Booking booking : bookings) {
			System.out.println(booking.toString());
			JsonObject jsonBooking = Json.createObjectBuilder()
					.add("id", booking.getId())
					.add("datetime",booking.getDate().toString())
					.add("name",booking.getName())
					.add("buid",booking.getBuid())
					.add("num_students",booking.getNumStuds())
					.add("destination",booking.getDest())
					.add("source",booking.getSource())
					.add("phone",booking.getPhone())
					.add("status",booking.getStatus())
					.build();
			list.add(jsonBooking);
		}
		
		return list.build().toString();
			
	}
	
	@GET
	@Path("/completed")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCompleted() {
		List<Booking> bookings = dao.getBookingsWithLimit("complete",20);
		JsonArrayBuilder list = Json.createArrayBuilder();
		for(Booking booking : bookings) {
			System.out.println(booking.toString());
			JsonObject jsonBooking = Json.createObjectBuilder()
					.add("id", booking.getId())
					.add("datetime",booking.getDate().toString())
					.add("name",booking.getName())
					.add("buid",booking.getBuid())
					.add("num_students",booking.getNumStuds())
					.add("destination",booking.getDest())
					.add("source",booking.getSource())
					.add("phone",booking.getPhone())
					.add("status",booking.getStatus())
					.build();
			list.add(jsonBooking);
		}
		
		return list.build().toString();
			
	}
}