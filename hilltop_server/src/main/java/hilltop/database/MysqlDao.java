package hilltop.database;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class MysqlDao {
	
	String host = "127.5.171.130";
	String port = "3306";
	String database = "hilltop";
	String connString = "jdbc:mysql://" + host + ":" + port + "/" + database;
	Connection con = null;
	
	private static MysqlDao instance = null;
	private MysqlDao( ) {
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		     e.printStackTrace();
		}

		try {
		    con = (Connection) DriverManager.getConnection(connString, "adminXme7LFJ", "rNtMskGUXpQJ");
		    System.out.println("connected");
		}
		catch (SQLException e) {
		    e.printStackTrace();
		}
	}
	
	public static MysqlDao getInstance() {
		if (instance == null) {
			instance = new MysqlDao();
		}
		return instance;
	}
	
	
	public List<Status> getBookings(int buid) {
		List<Status> results = new ArrayList<Status>();
		if (checkConnection()) {
			String sql = "select id,status from bookings where buid=? order by datetime desc limit 5";
			try {
				PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
				stmt.setInt(1, buid);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					String id = rs.getString(1);
					String status = rs.getString(2);
					Status st = new Status(id,status);
					results.add(st);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
		return results;
	}
	
	public List<Status> getPendingBookings(int buid) {
		List<Status> results = new ArrayList<Status>();
		if (checkConnection()) {
			String sql = "select id,status from bookings where buid=? and status ='approved' and datetime > DATE_SUB(NOW(), INTERVAL 2 HOUR) order by datetime desc limit 5";
			try {
				PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
				stmt.setInt(1, buid);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					String id = rs.getString(1);
					String status = rs.getString(2);
					Status st = new Status(id,status);
					results.add(st);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
		return results;
	}
	
	public List<Booking> getAllBookings(String type) {
		List<Booking> results = new ArrayList<Booking>();
		if (checkConnection()) {
			String sql = "select id,datetime,name,buid,num_students,destination,source,phone,status from bookings where status = ? order by datetime desc";
			try {
				PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
				stmt.setString(1, type);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					int id = rs.getInt(1);
					String datetime = rs.getDate(2).toString();
					String name = rs.getString(3);
					int buid = rs.getInt(4);
					int numStuds = rs.getInt(5);
					String dest = rs.getString(6);
					String source = rs.getString(7);
					String phone = rs.getString(8);
					String status = rs.getString(9);
					results.add(new Booking(id,datetime,name,buid,numStuds,dest,source,phone,status));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
		return results;
	}
	
	public List<Booking> getBookingsWithLimit(String type,int limit) {
		List<Booking> results = new ArrayList<Booking>();
		if (checkConnection()) {
			String sql = "select id,datetime,name,buid,num_students,destination,source,phone,status from bookings where status = ? order by datetime desc LIMIT "+limit;
			try {
				PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
				stmt.setString(1, type);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					int id = rs.getInt(1);
					String datetime = rs.getDate(2).toString();
					String name = rs.getString(3);
					int buid = rs.getInt(4);
					int numStuds = rs.getInt(5);
					String dest = rs.getString(6);
					String source = rs.getString(7);
					String phone = rs.getString(8);
					String status = rs.getString(9);
					results.add(new Booking(id,datetime,name,buid,numStuds,dest,source,phone,status));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
		return results;
	}
	
	public String addBooking(String name,int buid,int num,String to,String from,String phone) {
		if (checkConnection()) {
			String sql = "insert into bookings(datetime,name,buid,num_students,destination,source,phone,status) values (?,?,?,?,?,?,?,?)";
			try {
				PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
				stmt.setString(2,name);
				stmt.setInt(3,buid);
				stmt.setInt(4,num);
				stmt.setString(5,to);
				stmt.setString(6, from);
				stmt.setString(7, phone);
				stmt.setString(8,"waiting for approval");
				stmt.executeUpdate();
				ResultSet rs = stmt.getGeneratedKeys();
			    rs.next();
			    return rs.getInt(1)+"";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
		return -1+"";
	}
	
	public void changeStatus(String bookingId,String status) {
		if (checkConnection()) {
			String sql = "Update bookings set status = ? where id = ?";
			try {
				PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
				stmt.setString(1,status);
				stmt.setString(2, bookingId);
				stmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
	}
	
	public void markAsApproved(int bookingId) {
		if (checkConnection()) {
			String sql = "Update bookings set status = ? where id = ?";
			try {
				PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
				stmt.setString(1,"approved");
				stmt.setInt(2, bookingId);
				stmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
	}
	
	public void markAsRejected(int bookingId) {
		if (checkConnection()) {
			String sql = "Update bookings set status = ? where id = ?";
			try {
				PreparedStatement stmt = (PreparedStatement) con.prepareStatement(sql);
				stmt.setString(1,"rejected");
				stmt.setInt(2, bookingId);
				stmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			}
	}
	
	public int getLastBookingId() {
		int last = -1;
		if (checkConnection()) {
			String sql = "select id from bookings order by id desc limit 1";
			try {
				Statement stmt = (Statement) con.createStatement();
				ResultSet rs;
				rs = stmt.executeQuery(sql);
				
				while(rs.next()) {
					String lastId = rs.getString(1);
					last = Integer.parseInt(lastId);
					return last;
				}
				last = 0;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return last;
	}
	
	public boolean checkConnection() {
		if (con == null) {
			System.out.println("WARNING: Connection is closed, trying to reconnect");
			try {
				con = (Connection) DriverManager.getConnection(connString, "adminXme7LFJ", "rNtMskGUXpQJ");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			if (con == null)
				return false;
			else
				return true;
		}
		return true;
	}

}
