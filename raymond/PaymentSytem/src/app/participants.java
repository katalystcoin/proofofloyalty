package app;

import java.util.ArrayList;
import java.util.Arrays;

public class participants {

	private ArrayList<String> addr = new ArrayList<String>();
	private String hash;
	private String message;
	private String tx;
	private String id;
	
	public participants(String id,String hash, String message, String tx) {
		super();
		this.id = id;
		this.hash = hash;
		this.message = message;
		this.tx = tx;
		this.addr= new ArrayList<String>(Arrays.asList(message.split(",")));
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getAddr() {
		return addr;
	}
	public void setAddr(ArrayList<String> addr) {
		this.addr = addr;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTx() {
		return tx;
	}
	public void setTx(String tx) {
		this.tx = tx;
	}
}
