package org.iiinews.android;

import org.iiinews.android.config.Configuration;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.restexpress.RestExpress;

public abstract class Routes
{
	public static void define(Configuration config, RestExpress server)
    {
		//this url to read all the articles
		server.uri("articles.{format}", config.getArticleController())
			.action("readAll", HttpMethod.GET);
			//.name(Constants.Routes.SINGLE_SAMPLE);
		
		// this url to read one url details
		server.uri("articles/{"+ Constants.Url.ARTICLE_ID +"}.{format}", config.getArticleController())
			.method(HttpMethod.GET);

		//this url to read all the sources
		server.uri("sources.{format}", config.getSourceController())
			.action("readAll", HttpMethod.GET);
		
		/*server.uri("/your/route/here.{format}", config.getSampleController())
			.action("readAll", HttpMethod.GET)
			.method(HttpMethod.POST)
			.name(Constants.Routes.SAMPLE_COLLECTION);*/
		
// or...
//		server.regex("/some.regex", config.getRouteController());
    }
}
