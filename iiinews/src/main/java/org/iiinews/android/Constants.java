package org.iiinews.android;

public class Constants
{
	/**
	 * These define the URL parmaeters used in the route definition strings (e.g. '{userId}').
	 */
	public class Url
	{
		//Your URL parameter names here...
		//public static final String SAMPLE_ID = "sampleId";
		public static final String ARTICLE_ID = "id";
		public static final String LIMIT = "limit";
		public static final String OFFSET = "offset";
		public static final String LAST_UPDATE = "last_update";
		public static final String CHECK_REFRESHED = "check_refreshed";
		
	}

	/**
	 * These define the route names used in naming each route definitions.  These names are used
	 * to retrieve URL patterns within the controllers by name to create links in responses.
	 */
	public class Routes
	{
		//TODO: Your Route names here...
		public static final String SINGLE_SAMPLE = "sample.single.route";
		public static final String SAMPLE_COLLECTION = "sample.collection.route";
	}
}
