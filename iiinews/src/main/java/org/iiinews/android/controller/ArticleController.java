package org.iiinews.android.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.iiinews.android.Constants;
import org.iiinews.android.database.DBConnection;
import org.iiinews.android.model.Article;
import org.iiinews.android.model.CacheWithTime;
import org.iiinews.android.model.Paging;
import org.iiinews.android.model.ReadAllData;
import org.iiinews.android.model.ResponseData;
import org.iiinews.android.model.ResponseReadAllData;
import org.iiinews.android.model.Source;
import org.iiinews.android.utils.CustomeCosineSimilarity;
import org.iiinews.android.utils.StringUtils;
import org.restexpress.Request;
import org.restexpress.Response;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

public class ArticleController {
	public static final int DEFAULT_ARTICLES_LIMIT = 30;
	public static final int DEFAULT_LIMIT = 20;
	public static final long CACHE_TIME = 1800000L;// 30 mins
	public static final String HOME_NEWS_CACHE_KEY = "home";
	public static final String BUSINESS_NEWS_CACHE_KEY = "business";
	public static final String COMMUNITY_NEWS_CACHE_KEY = "community";
	public static final String EDUCATION_NEWS_CACHE_KEY = "education";
	public static final String ENTERTAINMENT_NEWS_CACHE_KEY = "entertainment";
	public static final String HEALTH_NEWS_CACHE_KEY = "health";
	public static final String LIFE_NEWS_CACHE_KEY = "life";
	public static final String NEWS_NEWS_CACHE_KEY = "news";
	public static final String OPINITONS_NEWS_CACHE_KEY = "opinions";
	public static final String POLICTICS_NEWS_CACHE_KEY = "polictics";
	public static final String SPORT_NEWS_CACHE_KEY = "sport";
	public static final String STYLE_NEWS_CACHE_KEY = "style";
	public static final String TECH_NEWS_CACHE_KEY = "technology";
	public static final String TRAVEL_NEWS_CACHE_KEY = "travel";
	public static final String WORLD_NEWS_CACHE_KEY = "world";
	public static final String SCIENCE_NEWS_CACHE_KEY = "science";
	private Connection dbConnection;
	private String host;
	private String passw;
	HashMap<String, Source> sourceMap = new HashMap<>();
	
	public ArticleController (String host, String passw) {
		this.host = host;
		this.passw = passw;
			try {
				dbConnection = DBConnection.getConnection(host, passw);
				String sql = "SELECT * FROM sources" ;
				// get all source first
				PreparedStatement preStm = dbConnection.prepareStatement(sql);
				preStm.execute();
				ResultSet sourceResults = preStm.getResultSet();
				while(sourceResults.next()){
					// TODO about and avatar is null for now
					Source source = new Source(sourceResults.getString(Source.SOURCE_NAME_FIELD),
							sourceResults.getString(Source.SOURCE_ID_FIELD), 
							null, null, sourceResults.getDouble(Source.SOURCE_REPUTATION_FIELD),
							sourceResults.getString(Source.SOURCE_URL_FIELD));
							source.setAvatarUrl(sourceResults.getString(Source.SOURCE_AVATAR_FIEDS));
							source.setFb_page_id(sourceResults.getString(Source.SOURCE_FB_PAGE));
					sourceMap.put(source.getId(), source);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public List<Article> read(Request request, Response response)
	{
		List<Article> articles = new ArrayList<Article>();
		//TODO later we do database here
		String id = request.getHeader(Constants.Url.ARTICLE_ID, "No Blog ID supplied");
		System.out.println("controller handled this: " + id);
		
		String sql = "SELECT id, url, facebook_id, facebook_plugin_id, text_html FROM articles WHERE id in ( %s )";
		sql = String.format(sql, id);
		System.out.println("query string: " + sql);
		try {
			if (dbConnection == null || dbConnection.isClosed())
				dbConnection = DBConnection.getConnection(host, passw);
			Statement stm = dbConnection.createStatement();
			ResultSet results = stm.executeQuery(sql);
			while(results.next()){
				Article	returnedArticle = new Article(results.getInt(Article.ID_FIELD), 
						results.getString(Article.URL_FIELD), "", 
						"", 
						"", null, 
						0, 0,
						0, 0, 
						0,
						false, 
						false, 
						0, 
						null, 
						results.getString(Article.FACEBOOK_ID_FIELD), 
						results.getString(Article.FACEBOOK_PLUGIN_ID));
				returnedArticle.setTextHtml(results.getString(Article.TEXT_HTML));
				articles.add(returnedArticle);
			}
		}catch (Exception e) {
			e.printStackTrace();
			try {
				dbConnection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			dbConnection = null;
		}
		
		return articles;
	}
	
	public Object readAll (Request request, Response response){
		// see whether client wanna check for refreshed
		//TODO do nothing for now
		boolean isCheckRefreshed = false;
		String checkRefreshedString = request.getHeader(Constants.Url.CHECK_REFRESHED);
		if (checkRefreshedString != null && ("true".equals(checkRefreshedString)|| "1".equals(checkRefreshedString)))
			isCheckRefreshed = true;
		
		//get category param
		String requestCategory = request.getHeader("category");
		
		//get source param
		String requestSource = request.getHeader("source");
		
		if (requestCategory != null || requestSource != null){
			return readAllWithParams(request, response, requestCategory, requestSource);
		}else {
			return readAllCategories(request, response, isCheckRefreshed);
		}
		
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public ResponseReadAllData readAllCategories (Request request, Response response, boolean isCheckRefreshed){
		ResponseReadAllData responseData = new ResponseReadAllData();
		ReadAllData data = new ReadAllData();
		Cache c = null;
		
		
		try {
			c = CacheManager.getInstance().getCache();
			//get home news
			CacheWithTime cachedObject = (CacheWithTime) c.retrieve(HOME_NEWS_CACHE_KEY);
			if (cachedObject == null){ 
				System.out.println("Home cache not found");
				throw new NoCacheException("Home cache not found");
			}
			List<Article> articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception("Home cache not found"); 
			if (articles.size() > 20)
				data.setHomePage(articles.subList(0, 20));
			else 
				data.setHomePage(articles);
			
			//get home business
			cachedObject = (CacheWithTime) c.retrieve(BUSINESS_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("business cache not found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception("business cache not found"); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setBusiness(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setBusiness(articles);

			//get home community
			cachedObject = (CacheWithTime) c.retrieve(COMMUNITY_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("community cache not found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception("community cache not found"); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setCommunity(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setCommunity(articles);
			
			//get home education
			cachedObject = (CacheWithTime) c.retrieve(EDUCATION_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("education cache not found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception("education cache not found"); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setEducation(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setEducation(articles);
			
			//get home entertainment
			cachedObject = (CacheWithTime) c.retrieve(ENTERTAINMENT_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("entertainment cache not found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception("entertainment cache not found"); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setEntertainment(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setEntertainment(articles);
			
			//get home health
			cachedObject = (CacheWithTime) c.retrieve(HEALTH_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("No cache found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception(); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setHealth(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setHealth(articles);
			
			//get home life
			cachedObject = (CacheWithTime) c.retrieve(LIFE_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("No cache found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception(); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setLife(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setLife(articles);
			
			//get home news
			cachedObject = (CacheWithTime) c.retrieve(NEWS_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("No cache found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception(); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setNews(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setNews(articles);
			
			//get home opinions
			cachedObject = (CacheWithTime) c.retrieve(OPINITONS_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("No cache found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception(); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setOpinions(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setOpinions(articles);
			
			//get home polictics
			cachedObject = (CacheWithTime) c.retrieve(POLICTICS_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("No cache found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception(); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setPolitics(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setPolitics(articles);
			
			//get home sport
			cachedObject = (CacheWithTime) c.retrieve(SPORT_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("No cache found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception(); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setSport(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setSport(articles);
			
			//get home style
			cachedObject =  (CacheWithTime) c.retrieve(STYLE_NEWS_CACHE_KEY);;
			if (cachedObject == null) throw new NoCacheException("No cache found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception(); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setStyle(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setStyle(articles);
			
			
			//get home technology
			cachedObject = (CacheWithTime) c.retrieve(TECH_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("No cache found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception(); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setTech(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setTech(articles);
			
			//get home travel
			cachedObject = (CacheWithTime) c.retrieve(TRAVEL_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("No cache found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception(); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setTravel(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setTravel(articles);
			
			//get home science
			cachedObject = (CacheWithTime) c.retrieve(SCIENCE_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("No cache found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception(); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setScience(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setScience(articles);
			
			
			//get home world
			cachedObject = (CacheWithTime) c.retrieve(WORLD_NEWS_CACHE_KEY);
			if (cachedObject == null) throw new NoCacheException("No cache found");
			articles = (List<Article>) cachedObject.getCachedObject();
			if (articles == null) throw new Exception(); 
			if (articles.size() > DEFAULT_ARTICLES_LIMIT)
				data.setWorld(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
			else 
				data.setWorld(articles);
			if (!isCheckRefreshed)
				responseData.setData(data);
			responseData.setRefreshed(false);
			responseData.setTime(new Date().getTime());
			System.out.println("Cache hit");
			return responseData;
		
		} catch (CacheException e1) {
			e1.printStackTrace();
			data = new ReadAllData();
		}catch (NoCacheException e1) {
			e1.printStackTrace();
			data = new ReadAllData();
		}catch (Exception e) {
			e.printStackTrace();
			data = new ReadAllData();
		}
		System.out.println("Cache misss");
		String sql = "SELECT * FROM sources;" +
		
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, normalized_title,  " +
				"((comment_count + share_count + like_count + twitter_count) * category_weight * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"ORDER BY point DESC LIMIT 60;" +
				
				
/*				 
				"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles ORDER BY point DESC LIMIT 100;" +*/
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='business' ORDER BY point DESC LIMIT 100;" +*/
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
		        "WHERE category_id = 'business'" +
				"ORDER BY point DESC LIMIT 60;" +
		        
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='community' ORDER BY point DESC LIMIT 100;" +*/
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'community'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='education' ORDER BY point DESC LIMIT 100;" +*/
				
				
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'education'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='entertainment' ORDER BY point DESC LIMIT 100;" +*/
				
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'entertainment'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='health' ORDER BY point DESC LIMIT 100;" +*/
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'health'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='life' ORDER BY point DESC LIMIT 100;" +*/
				
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'life'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='news' ORDER BY point DESC LIMIT 100;" +*/
				
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'news'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='opinions' ORDER BY point DESC LIMIT 100;" +*/
				
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'opinions'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='politics' ORDER BY point DESC LIMIT 100;" +*/
				
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'politics'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='sport' ORDER BY point DESC LIMIT 100;" +*/
				
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'sport'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='style' ORDER BY point DESC LIMIT 100;" +*/
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'style'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='tech' ORDER BY point DESC LIMIT 100;" +*/
				
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'tech'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='travel' ORDER BY point DESC LIMIT 100;" +*/
				
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'travel'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='science' ORDER BY point DESC LIMIT 100;" +*/
				
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'science'" +
				"ORDER BY point DESC LIMIT 60;" +
				
				
				/*"SELECT id, url, category_id, source_id, title,comment_count, twitter_count, (share_count) as share_count, like_count, " +
				"hot_point, is_top_story_on_their_site, is_on_home_page,  short_description,updated_time,  " +
				"(comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point, " +
				"thumbnail_url, facebook_id, facebook_plugin_id, text FROM articles WHERE category_id ='world' ORDER BY point DESC LIMIT 100;";*/
				
				"SELECT id, url, category_id, source_id, " +
				" title, comment_count, (twitter_count + share_count) as share_count, " +
				" like_count, short_description , updated_time, " + 
				"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
				"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
				"FROM articles " +
				"WHERE category_id = 'world'" +
				"ORDER BY point DESC LIMIT 60;" ;
		
				try {
					if (dbConnection == null || dbConnection.isClosed())
						dbConnection = DBConnection.getConnection(host, passw);
					//Statement stm = dbConnection.createStatement();
					PreparedStatement preStm = dbConnection.prepareStatement(sql);
					if (!preStm.execute())
						return responseData;
					// get all source first
					/*ResultSet sourceResults = preStm.getResultSet();
					while(sourceResults.next()){
						// TODO about and avatar is null for now
						Source source = new Source(sourceResults.getString(Source.SOURCE_NAME_FIELD),
								sourceResults.getString(Source.SOURCE_ID_FIELD), 
								null, null, sourceResults.getDouble(Source.SOURCE_REPUTATION_FIELD),
								sourceResults.getString(Source.SOURCE_URL_FIELD));
								source.setAvatarUrl(sourceResults.getString(Source.SOURCE_AVATAR_FIEDS));
								source.setFb_page_id(sourceResults.getString(Source.SOURCE_FB_PAGE));
						sourceMap.put(source.getId(), source);
					}*/
					
					long cachingMoment = new Date().getTime();
					//get top news for home page here
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						//TODO process this list later
						List<Article> homeNews = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObject = new CacheWithTime(homeNews, cachingMoment);
						c.store(HOME_NEWS_CACHE_KEY, cacheObject, CACHE_TIME);
						if (homeNews.size() > 21)
							data.setHomePage(homeNews.subList(0, 21));
						else 
							data.setHomePage(homeNews);
					}
					//get news for business
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> businessNews = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(businessNews, cachingMoment);
						c.store(BUSINESS_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (businessNews.size() > DEFAULT_ARTICLES_LIMIT)
							data.setBusiness(businessNews.subList(0, DEFAULT_ARTICLES_LIMIT));
						else
							data.setBusiness(businessNews);
					}
					//get news for community
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> comminityNews = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(comminityNews, cachingMoment);
						c.store(COMMUNITY_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (comminityNews.size() >= DEFAULT_ARTICLES_LIMIT)
							data.setCommunity(comminityNews.subList(0, DEFAULT_ARTICLES_LIMIT));
						else 
							data.setCommunity(comminityNews);
					}
					//get news for education
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(EDUCATION_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setEducation(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						data.setEducation(articles);
					}
					//get news for entertainment
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(ENTERTAINMENT_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size()> DEFAULT_ARTICLES_LIMIT)
							data.setEntertainment(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						else
							data.setEntertainment(articles);
					}
					//get news for heath
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(HEALTH_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setHealth(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						else
							data.setHealth(articles);
					}
					//get news for life 
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(LIFE_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if(articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setLife(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						else 
							data.setLife(articles);
					}
					//get news for news
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(NEWS_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setNews(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						else
							data.setNews(articles);
					}
					// get news for opinions
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(OPINITONS_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setOpinions(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						else
							data.setOpinions(articles);
					}
					// get news for polictics
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(POLICTICS_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setPolitics(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						else
							data.setPolitics(articles);
					}
					//get news for sport
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(SPORT_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setSport(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						else 
							data.setSport(articles);
					}
					//get news for style
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(STYLE_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setStyle(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						else 
							data.setStyle(articles);
					}
					//get news for technology
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(TECH_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setTech(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						else
							data.setTech(articles);
					}
					//get news for travel
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(TRAVEL_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setTravel(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						else
							data.setTravel(articles);
					}
					
					//get news for science
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(SCIENCE_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setScience(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						else
							data.setScience(articles);
					}
					
					//get world news
					if (preStm.getMoreResults()){
						ResultSet articlesResults = preStm.getResultSet();
						List<Article> articles = getArticlesFromResultSet(articlesResults, sourceMap);
						CacheWithTime cacheObj = new CacheWithTime(articles, cachingMoment);
						c.store(WORLD_NEWS_CACHE_KEY, cacheObj, CACHE_TIME);
						if (articles.size() > DEFAULT_ARTICLES_LIMIT)
							data.setWorld(articles.subList(0, DEFAULT_ARTICLES_LIMIT));
						data.setWorld(articles);
					}
				} catch (SQLException | ClassNotFoundException e) {
					e.printStackTrace();
					try {
						dbConnection.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					dbConnection = null;
				}
		responseData.setTime(new Date().getTime());
		responseData.setData(data);
		responseData.setRefreshed(true);

		return responseData;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public ResponseData readAllWithParams (Request request, Response response, String requestCategory, String requestSource){
		ResponseData responseData = new ResponseData();
		List<Article> articles = new ArrayList<>() ;
		Cache c = null;
		boolean isRefreshed = false;
		String requestSignature = null;
		HashMap<String, Source> sourceMap = new HashMap<>();
		if (dbConnection == null) {
			return responseData;
		}
		
		
		//get limit param
		int limit = 15;
		String limitString = request.getHeader(Constants.Url.LIMIT);
		try{
		if (limitString != null && limitString.length() > 0)
			limit = Integer.parseInt(limitString);
		if (limit > 15 || limit < 0) limit = 15;
		}catch (Exception e){
			e.printStackTrace();
		}
		
		//get offset param
		int offset = 0;
		String offsetString = request.getHeader(Constants.Url.OFFSET);
		try{
			if (offsetString != null && offsetString.length() > 0)
				offset = Integer.parseInt(offsetString);
			if (offset < 0) offset = 0;
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
		// get last time request
		long lastTimeRequest = 0;
		String lastTimeString = request.getHeader(Constants.Url.LAST_UPDATE);
		try{
			if (lastTimeString != null && lastTimeString.length() > 0)
				lastTimeRequest = Long.parseLong(lastTimeString);
			if (lastTimeRequest < 0) offset = 0;
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
		
		
		
		//handle caching here
		// this is signature as a key to retrieve cache
		requestCategory = requestCategory == null ? "" : requestCategory;
		requestSource = requestSource == null ? "" : requestSource;
		requestSignature = requestCategory + requestSource;
		System.out.println("caching key: " + requestSignature);
		try {
			 c = CacheManager.getInstance().getCache();
			 CacheWithTime cachedObject = (CacheWithTime) c.retrieve(requestSignature);
			 if (cachedObject == null)
				 articles = null;
			 else{ 
				 articles = (List<Article>) cachedObject.getCachedObject();
				 //check whether last request in client has been out date or not
				 // then requested offset and limit is no longer valid
				 if (lastTimeRequest < cachedObject.getCacheTime() - 2 * 60 * 60 * 1000){
					 System.out.println("last request too long ago");
					 offset = 0;
					 isRefreshed = true;
				 }
					 
			 }
			 
		} catch (CacheException e1) {
			e1.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (articles != null){
			System.out.println("Caching hit");
			// TODO may handle pagingnation here
			if (articles.size() == 0 || offset > articles.size()) {
					System.out.println("wrong offset");
					return responseData;
			}
			if (offset + limit > articles.size() - 1){
				System.out.println("offset + limit over capacity");
				Paging paging = new Paging(articles.size(), 0);
				responseData.setData(articles.subList(offset, articles.size()));
				responseData.setPaging(paging);
				if (isRefreshed)
					responseData.setRefreshed(true);
				else
					responseData.setRefreshed(false);
				return responseData;
			}
			if (offset + limit <= articles.size()){
				System.out.println("offset + limit okay");
				Paging paging = new Paging(offset + limit, articles.size() - offset - limit);
				responseData.setPaging(paging);
				responseData.setData(articles.subList(offset, offset + limit));
				if (isRefreshed)
					responseData.setRefreshed(true);
				else
					responseData.setRefreshed(false);
				return responseData;
			 }
			
			return responseData;
		}else {
			System.out.println("Caching miss");
			articles = new ArrayList<Article>();
		}
		
		
		
		
		// dump query for now 
		/*select title, source_id, (comment_count + share_count + like_count + twitter_count) as count, 
		 * (comment_count + share_count + like_count + twitter_count)/pow(UNIX_TIMESTAMP() - updated_time, 2) as point,
		 *  (UNIX_TIMESTAMP() - updated_time)/3600 as hours FROM articles ORDER BY point;*/ 
		
		
		String sql = "SELECT * FROM sources;" +
		 
						"SELECT id, url, category_id, source_id, " +
						" title, comment_count, (twitter_count + share_count) as share_count, " +
						" like_count, short_description , updated_time, " + 
						"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
						"((comment_count + share_count + like_count + twitter_count) * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
						"FROM articles " + getCategorySourceCondition(requestCategory, requestSource)
						+ " ORDER BY point DESC LIMIT 100";

		//TODO
			if ("home".equals(requestCategory))
				sql = "SELECT * FROM sources;" 
						+ "SELECT id, url, category_id, source_id, " +
						" title, comment_count, (twitter_count + share_count) as share_count, " +
						" like_count, short_description , updated_time, " + 
						"thumbnail_url, facebook_id, facebook_plugin_id, text, normalized_title, " +
						"((comment_count + share_count + like_count + twitter_count) * category_weight * reputation/pow(UNIX_TIMESTAMP() - updated_time + 16*60*60, 4)) as point " + 
						"FROM articles "  + getCategorySourceCondition(requestCategory, requestSource) +
						" ORDER BY point DESC LIMIT 100;";
		try {
			
			if (dbConnection == null || dbConnection.isClosed())
				dbConnection = DBConnection.getConnection(host, passw);
			
			//Statement stm = dbConnection.createStatement();
			PreparedStatement preStm = dbConnection.prepareStatement(sql);
			if (!preStm.execute())
				return responseData;
			// get all source first
			ResultSet sourceResults = preStm.getResultSet();
			while(sourceResults.next()){
				// TODO about and avatar is null for now
				Source source = new Source(sourceResults.getString(Source.SOURCE_NAME_FIELD),
						sourceResults.getString(Source.SOURCE_ID_FIELD), 
						null, null, sourceResults.getDouble(Source.SOURCE_REPUTATION_FIELD),
						sourceResults.getString(Source.SOURCE_URL_FIELD));
						source.setAvatarUrl(sourceResults.getString(Source.SOURCE_AVATAR_FIEDS));
				sourceMap.put(source.getId(), source);
			}
			
			//get articles here
			if (preStm.getMoreResults()){
				articles = getArticlesFromResultSet(preStm.getResultSet(), sourceMap);
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			try {
				dbConnection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			dbConnection = null;
		}
		
		
		// put result to cache
		long cachingTime = new Date().getTime();
		c.store(requestSignature, new CacheWithTime(articles, new Date().getTime()), CACHE_TIME);
		System.out.println("cached time: " + new Date().getTime());
		//handle paging
		if (lastTimeRequest < cachingTime - 2 * 60 * 60 * 1000 ){
			offset = 0; // offset forced to 0 cause data has been changed
			isRefreshed = true;
		}
		if (articles.size() == 0 || offset > articles.size() -1) {
			System.out.println("wrong offset");
			return responseData;
		}
		if (offset + limit > articles.size() - 1){
			System.out.println("offset + limit over capacity");
			Paging paging = new Paging(articles.size(), 0);
			responseData.setData(articles.subList(offset, articles.size()));
			responseData.setPaging(paging);
			if (isRefreshed)
			responseData.setRefreshed(true);
			return responseData;
		}
		if (offset + limit <= articles.size()){
			System.out.println("offset + limit okay");
			Paging paging = new Paging(offset + limit, articles.size() - offset - limit);
			responseData.setPaging(paging);
			responseData.setData(articles.subList(offset, offset + limit));
			if (isRefreshed)
			responseData.setRefreshed(true);
			return responseData;
		 }
		
		return responseData;
	}
	
	
	public static List<Article> getArticlesFromResultSet (ResultSet articlesResults, HashMap<String, Source> sourceMap) throws SQLException {
		ArrayList<Article> articles = new ArrayList<>();
		while(articlesResults.next()){
			Article article = new Article(articlesResults.getInt(Article.ID_FIELD), 
					articlesResults.getString(Article.URL_FIELD), articlesResults.getString(Article.SOURCE_ID_FIELD), 
					articlesResults.getString(Article.CATEGORY_ID_FIELD), 
					articlesResults.getString(Article.TITLE_FIELD), articlesResults.getString(Article.SHORT_DESCRIPTION), 
					articlesResults.getInt(Article.COMMENT_COUNT), articlesResults.getInt(Article.SHARE_COUNT),
					articlesResults.getInt(Article.LIKE_COUNT), 0, 
					0, false, false, 
					articlesResults.getLong(Article.TIME_FIELD), 
					articlesResults.getString(Article.THUMBNAIL_URL), 
					articlesResults.getString(Article.FACEBOOK_ID_FIELD), articlesResults.getString(Article.FACEBOOK_PLUGIN_ID));
			article.setSource(sourceMap.get(article.getSourceId()));
			article.setText(articlesResults.getString(Article.TEXT_FIELD));
			article.setHotPoint(articlesResults.getFloat(Article.HOT_POINT));
			article.setNormailizedTitle(articlesResults.getString(Article.TITLE_SIMPLIED));
			articles.add(article);
		}
		return cleanDuplicateArticles(articles);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String getCategorySourceCondition (String category, String source) {
		if ("home".equals(category))
			category = null;
		if (StringUtils.isEmpty(category) && StringUtils.isEmpty(source)) return "";
		else if (!StringUtils.isEmpty(category) && StringUtils.isEmpty(source))
			return String.format(" WHERE category_id = '%s' ", category);
		else if (StringUtils.isEmpty(category) && !StringUtils.isEmpty(source))
			return String.format(" WHERE source_id = '%s' ", source);
		else return String.format(" WHERE category_id = '%s' AND source_id = '%s' ", category, source);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String getSourceCondition (String source) {
		if (source == null) return "";
		else return String.format(" AND source_id = '%s'", source);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static List<Article> cleanDuplicateArticles (List<Article> articles) {
		ArrayList<Article> copyArticles = new ArrayList<Article>(articles);
		for (Iterator<Article> iterator = copyArticles.iterator(); iterator.hasNext();){
			Article article = iterator.next();
			for (Iterator<Article> iterator2 = articles.iterator(); iterator2.hasNext();){
				Article article2 = iterator2.next();
				if ((!article.equals(article2))){
					float titleSimilarity = CustomeCosineSimilarity.getSimilarity(article.getNormailizedTitle(), article2.getNormailizedTitle() );
					//System.out.println("articles1 title: " + article.getNormailizedTitle() );
					//System.out.println("articles2 title: " + article2.getNormailizedTitle() );
					if (titleSimilarity > 0.2){// articles sound similar if at least theirs title sound similar
						float contentSimilarity = CustomeCosineSimilarity.getSimilarity(article.getText(), article2.getText());
						if (titleSimilarity > 0.7 || titleSimilarity * 0.4 + contentSimilarity * 0.6 > 0.55){
							/*System.out.println( article.getId() + ": " + article.getTitle() 
							+ "<-> " +article2.getId()+ ":" + article2.getTitle());
							System.out.println( CustomeCosineSimilarity.getSimilarity(article.getText(), 
													article2.getText()));
							System.out.println(article.getText() + "\n\n\n");
							System.out.println(article2.getText() + "\n\n\n");*/
							if (article.getTime() > article2.getTime()){
								iterator.remove();
								break;
							}else {
								iterator2.remove();
							}
						}
					}
					
					
				}
			}
		}
		return articles;
	}
	
	private class NoCacheException extends Exception {
		public NoCacheException (String message) {
			super(message);
		}
		@Override
		public void printStackTrace() {
//			System.out.println();
			//super.printStackTrace();
		}
	}
}
