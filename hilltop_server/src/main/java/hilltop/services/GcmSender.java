package hilltop.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class GcmSender {

	private static String API_KEY = "AIzaSyDBsbGtEwdQJn2CGtCTOyMS09AodvgZiAw";
	
	public static boolean sendMessage(String message) {	
	try {
		JSONObject jGcmData = new JSONObject();
        JSONObject jData = new JSONObject();
        jData.put("message", message);
        // Where to send GCM message.
        jGcmData.put("to", "/topics/global");
        // What to send in GCM message.
        jGcmData.put("data", jData);

        // Create connection to send GCM Message request.
        URL url = new URL("https://android.googleapis.com/gcm/send");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "key=" + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // Send GCM message content.
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(jGcmData.toString().getBytes());

        // Read GCM response.
        InputStream inputStream = conn.getInputStream();
        String resp = IOUtils.toString(inputStream);
        System.out.println(resp);
        System.out.println("Check your device/emulator for notification or logcat for " +
                "confirmation of the receipt of the GCM message.");
        return true;
    } catch (IOException e) {
        System.out.println("Unable to send GCM message.");
        System.out.println("Please ensure that API_KEY has been replaced by the server " +
                "API key, and that the device's registration token is correct (if specified).");
        e.printStackTrace();
    }
	return false;
	}
}
