package org.iiinews.android.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.iiinews.android.database.DBConnection;
import org.iiinews.android.model.Source;
import org.restexpress.Request;
import org.restexpress.Response;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheManager;

public class SourceController {
	private Connection dbConnection;
	public static final long CACHE_TIME = 200000000L;
	private static final String SOURCES_KEY = "all_sources";
	private Cache c;
	private String host;
	private String passw;
	public SourceController() {
		super();
	}
	
	public SourceController (String dbHost, String dbPasswd) {
		this.host = dbHost;
		this.passw = dbHost;
		try {
			dbConnection = DBConnection.getConnection(dbHost, dbPasswd);
			c = CacheManager.getInstance().getCache();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Source> readAll(Request request, Response response) {
		List<Source> returnedList;
		try {
			returnedList = (List<Source>) c.retrieve(SOURCES_KEY);
			if (returnedList != null && returnedList.size() > 0)
				return returnedList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		returnedList  = new ArrayList<Source>();
		String sql = "SELECT * FROM sources";
		try {
			if (dbConnection == null || dbConnection.isClosed())
				dbConnection = DBConnection.getConnection(host, passw);
			//Statement stm = dbConnection.createStatement();
			PreparedStatement preStm = dbConnection.prepareStatement(sql);
			if (!preStm.execute())
				return returnedList;
			// get all source first
			ResultSet sourceResults = preStm.getResultSet();
			while(sourceResults.next()){
				// TODO about and avatar is null for now
				Source source = new Source(sourceResults.getString(Source.SOURCE_NAME_FIELD),
						sourceResults.getString(Source.SOURCE_ID_FIELD), 
						null, null, sourceResults.getDouble(Source.SOURCE_REPUTATION_FIELD),
						sourceResults.getString(Source.SOURCE_URL_FIELD));
						source.setAvatarUrl(sourceResults.getString(Source.SOURCE_AVATAR_FIEDS));
						source.setFb_page_id(sourceResults.getString(Source.SOURCE_FB_PAGE));
					returnedList.add(source);
			}
		}catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			try {
				dbConnection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			dbConnection = null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		c.store(SOURCES_KEY, returnedList);
		return returnedList;
	}
}
