package hilltop.services;
 
 
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import hilltop.database.MysqlDao;

import java.io.StringReader;
import java.util.Calendar;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;

@Path("/bookHilltop")
public class BookHilltop {

	MysqlDao dao = MysqlDao.getInstance();
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String driverLogin(String bookingDetails) {
		
		JsonReader reader = Json.createReader(new StringReader(bookingDetails));
		JsonObject bookingDetailsJson = reader.readObject();
		reader.close();
		String name = bookingDetailsJson.getString("name");
		int id = Integer.parseInt(bookingDetailsJson.getString("buid"));
		int num = Integer.parseInt(bookingDetailsJson.getString("num"));
		String to = bookingDetailsJson.getString("to");
		String from = bookingDetailsJson.getString("from");
		String phone = bookingDetailsJson.getString("phone");
		JsonObject result = null;
		if (!isWithinOpeartionHours()) {
			result =  Json.createObjectBuilder().add("status","failed : "+"Not within opearinting hours").build();
			//return result.toString();
		}
		try{
			String curr = dao.addBooking(name, id, num, to, from,phone);
			result =  Json.createObjectBuilder().add("status","success").add("approval_status","waiting for approval").add("booking_id",curr+"").build();
		}catch(Exception e) {
			result =  Json.createObjectBuilder().add("status","failed : "+e.getMessage()).build();
		}
		
		
		
		return result.toString();
	}
	
	public boolean isWithinOpeartionHours() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if ((hour >= 19 && hour <= 24) || (hour >=0 && hour<=3))
            return true;
        else
            return false;

    }
}