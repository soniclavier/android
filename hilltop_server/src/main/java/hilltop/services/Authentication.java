package hilltop.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import hilltop.database.MysqlDao;

import java.io.StringReader;
import java.util.Random;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

@Path("/auth")
public class Authentication {
	public static String SECRET_KEY = "hilltop_bradley_key_!@#"+System.currentTimeMillis();
	
	
	
	public static void main(String[] args) {
		jwt();
	}
	
	
	public static void jwt() {
		Key key = MacProvider.generateKey();
		String s = Jwts.builder().setSubject("Joe").signWith(SignatureAlgorithm.HS512, key).compact();
		try{
			Jwts.parser().setSigningKey(key).parseClaimsJws(s);
		} catch (SignatureException e) {

		    //don't trust the JWT!
		}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/login")
	public String login(String creds) {
		JsonReader reader = Json.createReader(new StringReader(creds));
		JsonObject credsJson = reader.readObject();
		reader.close();
		String username = credsJson.getString("username");
		String password = credsJson.getString("password");
		if (username.equals("Admin") && password.equals("bu@cruiser")) {
			String s = Jwts.builder().setSubject(username).signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
			JsonObject success = Json.createObjectBuilder().add("status","success").add("token",s).build();
			return success.toString();
		} else {
			JsonObject failed = Json.createObjectBuilder().add("status","failed").add("token","").build();
			return failed.toString();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/jwt/logout")
	public String logout() {
		SECRET_KEY = "hilltop_bradley_key_!@#"+System.currentTimeMillis();
		JsonObject success = Json.createObjectBuilder().add("status","success").build();
		return success.toString();
	}
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/validate")
	public String validate(String tokenKeyValue) {
		System.out.println("in validate "+tokenKeyValue);
		if (!tokenKeyValue.contains("=")) {
			JsonObject failed = Json.createObjectBuilder().add("status","failed").build();
			return failed.toString();
		}
		String token = tokenKeyValue.substring(tokenKeyValue.indexOf("=")+1);
		System.out.println("token =  "+token);
		try {
			Jwts.parser().setSigningKey(Authentication.SECRET_KEY).parseClaimsJws(token);
			JsonObject success = Json.createObjectBuilder().add("status","success").build();
			return success.toString();
		}catch (SignatureException se) {
			JsonObject failed = Json.createObjectBuilder().add("status","failed").build();
			return failed.toString();
		}
	}
}
