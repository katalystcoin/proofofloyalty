import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class Main {

	public static void main(String[] args) {
		if (!isFileshipAlreadyRunning()) {
			System.out.println("There is already an instance running. Please exit that one first.");
			System.exit(0);
		}
		String votingAddress = "3P6o6H66hPrSbtLwvox8Bss4krSnKmyFkum";
		String savedAddress = "3PBA4kzzWgzRCbRZhKaYk6ihrWJdKmEYnEi";
		String richlist = "HxQSdHu1X4ZVXmJs232M6KfZi78FseeWaEXJczY6UxJ3";
		String apiKey = "";
		int stopheight = 743369;
		int startHeight = 743553;
		int yesVotes = 0;
		int noVotes = 0;

		program(votingAddress, stopheight, startHeight, yesVotes, noVotes, savedAddress, apiKey, richlist);

	}

	public static void program(String address, int stop, int start, int yesVotes, int noVotes, String saved, String key,
			String richlist) {
		// while (true) {

		List<String> addresses = new ArrayList<String>();
		String participants = "";
		int amountYes = 0;
		int amountNo = 0;

		try {
			List<Transaction> transactions = new ArrayList<Transaction>();
			WavesApi api = new WavesApi("node.callsheep.us:7000");
			JSONObject richObj = new JSONObject(api.getRichList(richlist));

			transactions = api.getLastPayments(address, stop, start);

			for (Transaction trans : transactions) {
				if (!addresses.contains(trans.getSender())) {
					addresses.add(address);
					int amount = richObj.getInt(address);
					if (amount != 0) {
						if (trans.getAttachment().toLowerCase().contains("yes")) {
							yesVotes++;
							amountYes += amount;
						} else {
							noVotes++;
							amountNo += amount;
						}
						participants += trans.getSender() + ",";
					}

				}

			}
			System.out.println("Yes votes: " + yesVotes + " No votes: " + noVotes);
			System.out.println("Amount yes: " + amountYes + " Amount no: " + amountNo);
			String votes = "Yes: " + amountYes + " No: " + amountNo;
			Log(api, participants, key, saved, " participants",1);
			Log(api, votes, key, saved, " votes",2);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			// program(address, stop, start, yesVotes, noVotes, saved, key);
		}
		// }
	}

	private static void Log(WavesApi api, String message, String key, String saved, String output, int type) {
		Api db = new Api();
		String hashStr = api.HashSecure(message, key).getString("hash");
		System.out.println(message+"    "+hashStr);

		String attachmentEnd = Base58.encode(hashStr.getBytes());
		String tx = api.StoreToChain(saved, key, attachmentEnd);
		JSONObject txObj = new JSONObject(tx);

		byte[] attach = Base58.decode((String) txObj.get("attachment"));
		String attache = new String(attach);
		if (hashStr.equals(attache)) {
			System.out.println("Waiting for loggin for: " + output);
			while (api.getUtxInfo((String) txObj.get("id")) != null) {
				try {
					Thread.sleep(300);
					System.out.println("Waiting for loggin for: " + output);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			db.postReference(hashStr, message, (String) txObj.get("id"), type);
			System.out.println("Succesfull logged: " + output);
		} else {
			System.out.println("Something failed");
			System.out.println(attache + " does not match " + hashStr);
		}
	}

	private static boolean isFileshipAlreadyRunning() {
		// socket concept is shown at
		// http://www.rbgrn.net/content/43-java-single-application-instance
		// but this one is really great
		try {
			final File file = new File("PriceReserved.txt");
			final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
			final FileLock fileLock = randomAccessFile.getChannel().tryLock();
			if (fileLock != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							fileLock.release();
							randomAccessFile.close();
							file.delete();
						} catch (Exception e) {
							// log.error("Unable to remove lock file: " +
							// lockFile, e);
						}
					}
				});
				return true;
			}
		} catch (Exception e) {
			// log.error("Unable to create and/or lock file: " + lockFile, e);
		}
		return false;
	}

}
