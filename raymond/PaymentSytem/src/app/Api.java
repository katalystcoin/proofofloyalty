package app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

public class Api {

	private String apiUrl;
	public Api() {
		apiUrl = "http://localhost:3000/api/";

	}

	public Boolean postReference(String hash, String message, String tx, int type) {
		String total = "";
		try {
			JSONObject parent=new JSONObject();
			parent.put("hash", hash);
			parent.put("message", message);
			parent.put("tx", tx);
			parent.put("type", type);
			
			URL url = new URL(apiUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);
			
			OutputStream os = conn.getOutputStream();
			os.write(parent.toString().getBytes("UTF-8"));
			os.close();

			if (conn.getResponseCode() != 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));

				String output;
				String temp = null;
				while ((output = br.readLine()) != null) {
					temp += output;
				}
				System.out.println(temp);
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;

			while ((output = br.readLine()) != null) {
				total += output;
			}

			conn.disconnect();
			System.out.println(output);
		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		
		return true;
		

	}

}
