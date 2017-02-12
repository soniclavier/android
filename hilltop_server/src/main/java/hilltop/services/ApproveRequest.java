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

 
@Path("/jwt/approveRequest")
public class ApproveRequest {
 
	MysqlDao dao = MysqlDao.getInstance();
	
	@Path("{bookingid}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public String approveRequest(@PathParam("bookingid") String id) {
		int bookingid = Integer.parseInt(id);
		dao.markAsApproved(bookingid);
		JsonObject jsonBooking = Json.createObjectBuilder().add("status","updated").build();
		return jsonBooking.toString();
			
	}
}