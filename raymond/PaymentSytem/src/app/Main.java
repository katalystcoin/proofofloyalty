package app;

import java.util.ArrayList;

import org.json.JSONObject;

public class Main {

	public static WavesApi api = new WavesApi("node.callsheep.us:7000");

	public static void main(String[] args) {
		String Url = "blackturtle.eu";
		String user = "bramnfx154_raymond";
		String datab = "bramnfx154_lpos";
		String password = "D6RX77k9G";
		String apipass = "ClausNode2017";
		String addrS = "3PBA4kzzWgzRCbRZhKaYk6ihrWJdKmEYnEi";
		boolean running = true;
		while(running){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Database db = new Database(Url, password, user, "3306", datab);
			String lastPayment = db.getLastPayment(5);
			int index = 0;
			if (lastPayment == null || lastPayment.isEmpty()) {
				index = 0;
			} else {
				index = Integer.parseInt(lastPayment);
			}
			ArrayList<participants> part = db.getParticipantsAfterX(1, index);
			if (part != null) {
				for (participants p : part) {
					if (verify(p.getTx(), p.getMessage(), apipass, p.getHash())) {
						System.out.println(p.getMessage());
						for (String a : p.getAddr()) {
							String message = "id:" + p.getId() + ",tx:" + p.getTx() + ",addr:" + a;
							String hashMessage = api.HashSecure(message, apipass).getString("hash");
							String encodedHash = Base58.encode(hashMessage.getBytes());
							String tx = api.StoreToChain(null, null, 1, 100000, a, addrS, apipass, encodedHash)
									.getString("id");
							Api npm = new Api();
							while (api.getUtxInfo(tx) != null) {
								try {
									Thread.sleep(300);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							npm.postReference(hashMessage, message, tx, 5);
							System.out.println(tx);
						}
					} else {
						System.out.println("Hashed error, tx will not be processed.");
					}

				}
			}
		}
		
	}
	public static boolean verify(String txId, String message, String apipass, String hash) {
		String newHash = api.HashSecure(message, apipass).getString("hash");
		JSONObject temp = null;
		try {
			temp = api.getTxInfo(txId);
		} catch (Error e) {
			System.out.println(e.getMessage() + " Tx id: " + txId);
		}
		String txIdHash = "";
		if (temp != null) {
			txIdHash = temp.getString("attachment");
			txIdHash = new String(Base58.decode(txIdHash));
		} else {
			System.out.println("Tx lookup went wrong. Tx will not be processed.");
		}
		if (newHash.equals(hash) && txIdHash.equals(hash) && txIdHash.equals(newHash)) {
			return true;
		} else {
			return false;
		}
	}

}
