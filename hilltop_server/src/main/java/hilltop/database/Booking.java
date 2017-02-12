package hilltop.database;

public class Booking {

	private int id;
	private String date;
	private String name;
	private int buid;
	private int numStuds;
	private String dest;
	private String source;
	private String status;
	private String phone;
	
	public Booking(int id, String date,String name,int buid, int numStuds, String dest, String source, String phone,String status) {
		this.id = id;
		this.date = date;
		this.name = name;
		this.buid = buid;
		this.numStuds = numStuds;
		this.dest = dest;
		this.source = source;
		this.phone = phone;
		this.status = status;
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getBuid() {
		return buid;
	}
	public void setBuid(int buid) {
		this.buid = buid;
	}
	public int getNumStuds() {
		return numStuds;
	}
	public void setNumStuds(int numStuds) {
		this.numStuds = numStuds;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString(){ 
		return id+","+date+","+name+","+buid+","+numStuds+","+dest+","+source+","+phone+","+status;
	}
}
