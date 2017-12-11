import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class WavesApi {
	private String url;

	public WavesApi(String url) {
		this.url = url;
	}

	public String getLastPayments(String addr, int limit) {
		String total = "";
		try {
			URL url = new URL("http://" + this.url + "/transactions/address/" + addr + "/limit/" + limit);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
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

		return total;
	}

	public List<Transaction> getLastPayments(String addr, int lowBlockheight, int highBlockheight) {
		List<Transaction> trans = new ArrayList<Transaction>();
		try {
			for (int i = lowBlockheight; i < highBlockheight; i++) {
				String total = "";
				URL url = new URL("http://" + this.url + "/blocks/at/" + i);
				System.out.println(url.toString());
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output;

				while ((output = br.readLine()) != null) {
				
					total += output;
				}
				JSONObject objTrans = new JSONObject(total);
				JSONArray array = objTrans.getJSONArray("transactions");
				for (int j = 0; j< array.length(); j++) {
					JSONObject obj = (JSONObject) array.get(j);
					Transaction transaction = new Transaction();

					
					if (obj.getInt("type") == 4 
							&& obj.getString("recipient").equals(addr)) {
						if(obj.getString("attachment") != null && obj.getString("attachment") !="" ){
							byte[] decodedBytes = Base58.decode(obj.getString("attachment"));
							transaction.setAttachment(new String(decodedBytes));	
						}else{
							transaction.setAttachment("No attachment available.");
						}
						
						transaction.setSender(obj.getString("sender"));
						transaction.setReceiver(obj.getString("recipient"));
						transaction.setTimestamp(obj.getLong("timestamp"));
						transaction.setAmount(obj.getInt("amount"));
						if (obj.get("assetId").equals(null)) {
							transaction.setAssetID("Waves");
						} else {
							transaction.setAssetID(obj.getString("assetId"));
						}

						transaction.setId(obj.getString("id"));

						//System.out.println(transaction.getAttachment());
						if(transaction.getAttachment().toLowerCase().contains("yes") || transaction.getAttachment().toLowerCase().contains("no")){
							trans.add(transaction);
						}
					}
				}
				//System.out.println(total);
				conn.disconnect();

			}
		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

		return trans;

	}

	public String getAssetInfo(String assetId) {
		String total = "";
		try {
			URL url = new URL("http://" + this.url + "/transactions/info/" + assetId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
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

		return total;
	}
	
	public String StoreToChain(String addr, String apipass, String attachment) {
		String total = "";
		try {
			JSONObject parent=new JSONObject();
			parent.put("amount", 1);
			parent.put("fee", 100000);
			parent.put("sender", addr);
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
		return total;
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

	public String getRichList(String assetId) {
		String total = "";
		try {
			URL url = new URL("http://" + this.url + "/assets/"+assetId+"/distribution");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
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

		return total;
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
				System.out.println("Nothing found :"+ temp);
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
		System.out.println("Something found :"+ total);
		return new JSONObject(total);
	}
}
