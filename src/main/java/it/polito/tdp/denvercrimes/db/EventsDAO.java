package it.polito.tdp.denvercrimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.denvercrimes.model.Event;


public class EventsDAO {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Integer> getAnni(){
		String sql = "SELECT DISTINCT(reported_date) AS data " + 
				"FROM `events`";
		
		List<Integer> anni = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				LocalDateTime t = rs.getTimestamp("data").toLocalDateTime();
				if(!anni.contains(t.getYear())) {
					anni.add(t.getYear());
				}
			}
			
			conn.close();
			return anni;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al database", e);
		}
	}
	
	public List<Integer> getGiorni(){
		String sql = "SELECT DISTINCT(reported_date) AS data " + 
				"FROM `events`";
		
		List<Integer> giorni = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				LocalDateTime t = rs.getTimestamp("data").toLocalDateTime();
				if(!giorni.contains(t.getDayOfMonth())) {
					giorni.add(t.getDayOfMonth());
				}
			}
			
			conn.close();
			return giorni;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al database", e);
		}
	}
	
	public List<Integer> getMesi(){
		String sql = "SELECT DISTINCT(reported_date) AS data " + 
				"FROM `events`";
		
		List<Integer> mesi = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				LocalDateTime t = rs.getTimestamp("data").toLocalDateTime();
				if(!mesi.contains(t.getMonthValue())) {
					mesi.add(t.getMonthValue());
				}
			}
			
			conn.close();
			return mesi;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al database", e);
		}
	}
	
	public List<Integer> getDistretti(){
		String sql = "SELECT DISTINCT(district_id) AS id " + 
				"FROM `events`";
		
		List<Integer> distretti = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				distretti.add(rs.getInt("id"));
			}
			conn.close();
			return distretti;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al database", e);
		}
	}
	
	public Double getLatitudineMedia(Integer distretto, Integer anno) {
		String sql = "SELECT AVG(geo_lat) AS latMedia " + 
				"FROM denver_crimes.`events` " + 
				"WHERE district_id = ? AND YEAR(reported_date) = ?";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st  =conn.prepareStatement(sql);
			st.setInt(1, distretto);
			st.setInt(2, anno);
			
			ResultSet rs = st.executeQuery();
			rs.next();
			Double latMedia = rs.getDouble("latMedia");
			conn.close();
			return latMedia;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al database", e);
		}
	}
	
	public Double getLongitudineMedia(Integer distretto, Integer anno) {
		String sql = "SELECT AVG(geo_lon) AS lonMedia " + 
				"FROM denver_crimes.`events` " + 
				"WHERE district_id = ? AND YEAR(reported_date) = ?";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, distretto);
			st.setInt(2, anno);
			
			ResultSet rs = st.executeQuery();
			rs.next();
			Double lonMedia = rs.getDouble("lonMedia");
			conn.close();
			return lonMedia;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al database", e);
		}
	}

	public Integer getDistrettoMin(Integer anno) {
		
		String sql = "SELECT district_id AS id " + 
				"FROM denver_crimes.`events` " + 
				"WHERE YEAR(reported_date) = ? " + 
				"GROUP BY district_id " + 
				"ORDER BY COUNT(*) ASC " + 
				"LIMIT 1";
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			
			ResultSet rs = st.executeQuery();
			rs.next();
			Integer minD = rs.getInt("id");
			conn.close();
			return minD;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al database", e);
		}
	}

	public List<Event> listAllEventsByDate(Integer anno, Integer mese, Integer giorno){
		String sql = "SELECT * FROM events WHERE Year(reported_date) = ? "
				+ "AND Month(reported_date) = ? AND Day(reported_date) = ?";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno);
			st.setInt(2, mese);
			st.setInt(3, giorno);
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
}

