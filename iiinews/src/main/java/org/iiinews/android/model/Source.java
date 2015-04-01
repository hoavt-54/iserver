package org.iiinews.android.model;

public class Source extends BasedObject {
	
	public static final String  SOURCE_ID_FIELD = "source_id";
	public static final String SOURCE_NAME_FIELD = "name";
	public static final String SOURCE_URL_FIELD = "url";
	public static final String SOURCE_REPUTATION_FIELD = "reputation";
	public static final String SOURCE_AVATAR_FIEDS = "avatar_url";
	public static final String SOURCE_FB_PAGE = "fb_page_id";
	
	private String name;
	private String id;
	private String avatarUrl;
	private String about;
	private transient double reputation;
	private String url ;
	private String fb_page_id;
	
	
	
	
	public Source(String name, String id, String avatarUrl, String about,
			double reputation, String url) {
		super();
		this.name = name;
		this.id = id;
		this.avatarUrl = avatarUrl;
		this.about = about;
		this.reputation = reputation;
		this.url = url;
	}

	public double getReputation() {
		return reputation;
	}

	public void setReputation(double reputation) {
		this.reputation = reputation;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getFb_page_id() {
		return fb_page_id;
	}

	public void setFb_page_id(String fb_page_id) {
		this.fb_page_id = fb_page_id;
	}
	
	
}
