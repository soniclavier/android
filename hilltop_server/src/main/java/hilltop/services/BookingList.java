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

 
@Path("/bookingList")
public class BookingList {
 
	MysqlDao dao = MysqlDao.getInstance();
	
	@Path("{buid}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	
	public String getBookingList(@PathParam("buid") String id) {
		int buid = Integer.parseInt(id);
		List<Status> bookings = dao.getBookings(buid);
		JsonArrayBuilder list = Json.createArrayBuilder();
		System.out.println("number of bookings for "+buid+" = "+bookings.size() );
		for(Status booking : bookings) {
			JsonObject jsonBooking = Json.createObjectBuilder().add("booking_id", booking.id).add("booking_status",booking.status).build();
			list.add(jsonBooking);
		}
		
		return list.build().toString();
			
	}
}