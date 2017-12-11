package app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import org.json.JSONObject;

public class WavesApi {
	private String url;

	public WavesApi(String url) {
		this.url = url;
	}

	public JSONObject getTxInfo(String assetId) {
		String total = "";
		try {
			URL url = new URL("http://" + this.url + "/transactions/info/" + assetId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));

				String output;
				String temp = "";
				while ((output = br.readLine()) != null) {
					temp += output;
				}
				if(new JSONObject(temp).getString("status").equals("error")) {
					return null;
				}
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;

			while ((output = br.readLine()) != null) {
				total += output;
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

		return new JSONObject(total);
	}
	
	public JSONObject StoreToChain(String assetId, String feeAssetId,int amount, int fee,String addr,String addrS, String apipass, String attachment) {
		String total = "";
		try {
			JSONObject parent=new JSONObject();
			parent.put("amount", amount);
			parent.put("fee", fee);
			parent.put("assetId",assetId );
			parent.put("feeAssetId",feeAssetId );
			parent.put("sender", addrS);
			parent.put("recipient", addr);
			parent.put("attachment", attachment);
			
			URL url = new URL("http://" + this.url + "/assets/transfer");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("api_key", apipass);
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

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		JSONObject signed = new JSONObject(total);
		signed.put("feeAssetId",signed.remove("feeAsset"));
		//System.out.println(signed.toString());
		total="";
		
		try {
			
			URL url = new URL("http://" + this.url + "/assets/broadcast/transfer");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("api_key", apipass);
			conn.setDoOutput(true);
			
			OutputStream os = conn.getOutputStream();
			os.write(signed.toString().getBytes("UTF-8"));
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

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		//System.out.println(total);
		return new JSONObject(total);
	}

	public JSONObject HashSecure(String message, String apipass) {
		String total = "";
		try {
			
			URL url = new URL("http://" + this.url + "/utils/hash/secure");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("api_key", apipass);
			conn.setDoOutput(true);
			
			OutputStream os = conn.getOutputStream();
			os.write(message.getBytes());
			os.close();

			if (conn.getResponseCode() != 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));

				String output;
				String temp = "";
				while ((output = br.readLine()) != null) {
					temp += output;
				}
				System.out.println(temp);
				throw new RuntimeException("Failed : HTTP error code in function hash : " + conn.getResponseCode());
				
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;

			while ((output = br.readLine()) != null) {
				total += output;
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		JSONObject answer = new JSONObject(total);
		
		return answer;
	}
	public JSONObject getUtxInfo(String utx) {
		String total = "";
		try {
			URL url = new URL("http://" + this.url + "/transactions/unconfirmed/info/" + utx);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));

				String output;
				String temp = "";
				while ((output = br.readLine()) != null) {
					temp += output;
				}
				if(new JSONObject(temp).getString("details").equals("Transaction is not in UTX")) {
					return null;
				}
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;

			while ((output = br.readLine()) != null) {
				total += output;
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

		return new JSONObject(total);
	}
}
