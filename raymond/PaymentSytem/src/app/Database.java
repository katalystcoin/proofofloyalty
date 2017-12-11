package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database {
	private Connection conn;

	public Database(String url, String password, String username, String portnummer, String dbase) {
		super();
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://" + url + ":" + portnummer + "/"+dbase, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public String getLastPayment(int type) {
		ResultSet rs;
		try {
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM lpos WHERE type = ? ORDER BY id DESC LIMIT 1");
			statement.setInt(1, type);
			rs = statement.executeQuery();
			if(rs.next()) {
				return rs.getString("id");
			}else {
				return null;
			}
				
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public ArrayList<participants> getParticipantsAfterX(int type, int index) {
		ResultSet rs;
		ArrayList<participants> part = new ArrayList<>();
		try {
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM lpos WHERE type = ? AND id > ?");
			statement.setInt(1, type);
			statement.setInt(2, index);
			rs = statement.executeQuery();
			while(rs.next()) {
				part.add(new participants(rs.getString("id"), rs.getString("hash"), rs.getString("message"), rs.getString("tx")));
				return part;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return part;
	}


}
