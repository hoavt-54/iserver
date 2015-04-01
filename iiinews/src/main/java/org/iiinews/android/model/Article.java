package org.iiinews.android.model;


public class Article extends BasedObject{
	public static final String ID_FIELD = "id";
	public static final String URL_FIELD = "url";
	public static final String TITLE_FIELD = "title";
	public static final String FACEBOOK_ID_FIELD = "facebook_id";
	public static final String FACEBOOK_PLUGIN_ID = "facebook_plugin_id";
	public static final String SOURCE_ID_FIELD = "source_id";
	public static final String CATEGORY_ID_FIELD = "category_id";
	public static final String COUNTRY_FIELD = "country";
	public static final String COMMENT_COUNT = "comment_count";
	public static final String LIKE_COUNT = "like_count";
	public static final String SHARE_COUNT = "share_count";
	public static final String TWITTER_COUNT = "twitter_count";
	public static final String HOT_POINT = "point"; 
	public static final String IS_TOP_STORY = "is_top_story_on_their_site";
	public static final String IS_ON_HOME_PAGE = "is_on_home_page";
	public static final String TIME_FIELD = "updated_time";
	public static final String THUMBNAIL_URL = "thumbnail_url";
	public static final String SHORT_DESCRIPTION = "short_description";
	public static final String TEXT_FIELD = "text";
	public static final String TITLE_SIMPLIED = "normalized_title";
	public static final String TEXT_HTML = "text_html";
	
	
	
	private int id;
	private String url;
	private transient String sourceId;
	private Source source;
	private String category;
	private String title;
	private String shortDescription;
	private int commentCount;
	private int shareCount;
	private int likeCount;
	private int twitterCount;
	private String country;
	private float hotPoint;
	private transient boolean isTopStory;
	private transient boolean isOnHomePage;
	private long time;
	private String thumbnailUrl;
	private String facebookId;
	private String facebookPluginId;
	private transient String text;
	private String textHtml;
	private transient String normailizedTitle;
	
	public Article () {
		
	}

	public Article (String url, String title){
		this.title = title;
		this.url = url;
	}
	
	public Article (int id, String url,String sourceId,String category, String title, String shortDescription,
			int commentCount, int shareCount, int likeCount, int twitterCount,  float hotPoint, boolean isTopStory, boolean isOnHomePage,
			long time, String thumbnailUrl, String facebookId, String facebookPluginId){
		this.id = id;
		this.url = url;
		this.sourceId = sourceId;
		this.category = category;
		this.title = title;
		this.shortDescription = shortDescription;
		this.commentCount = commentCount;
		this.shareCount = shareCount;
		this.likeCount = likeCount;
		this.twitterCount = twitterCount;
		this.hotPoint = hotPoint;
		this.isTopStory = isTopStory;
		this.isOnHomePage = isOnHomePage;
		this.time = time;
		this.thumbnailUrl = thumbnailUrl;
		this.facebookId = facebookId;
		this.facebookPluginId = facebookPluginId;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public int getTwitterCount() {
		return twitterCount;
	}

	public void setTwitterCount(int twitterCount) {
		this.twitterCount = twitterCount;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getNormailizedTitle() {
		return normailizedTitle;
	}

	public void setNormailizedTitle(String normailizedTitle) {
		this.normailizedTitle = normailizedTitle;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getShareCount() {
		return shareCount;
	}

	public void setShareCount(int shareCount) {
		this.shareCount = shareCount;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public float getHotPoint() {
		return hotPoint;
	}

	public void setHotPoint(float hotPoint) {
		this.hotPoint = hotPoint;
	}

	public boolean isTopStory() {
		return isTopStory;
	}

	public void setTopStory(boolean isTopStory) {
		this.isTopStory = isTopStory;
	}

	public boolean isOnHomePage() {
		return isOnHomePage;
	}

	public void setOnHomePage(boolean isOnHomePage) {
		this.isOnHomePage = isOnHomePage;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getFacebookPluginId() {
		return facebookPluginId;
	}

	public void setFacebookPluginId(String facebookPluginId) {
		this.facebookPluginId = facebookPluginId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTextHtml() {
		return textHtml;
	}

	public void setTextHtml(String textHtml) {
		this.textHtml = textHtml;
	}
	
	public int getCount () {
		return shareCount + commentCount + likeCount + twitterCount;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Object){
			Article objArt = (Article) obj;
			return objArt.id == this.id;
		}
		return false;
	}
}
