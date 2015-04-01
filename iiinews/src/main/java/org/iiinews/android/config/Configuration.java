package org.iiinews.android.config;

import java.util.Properties;

import org.iiinews.android.controller.ArticleController;
import org.iiinews.android.controller.SampleController;
import org.iiinews.android.controller.SourceController;
import org.restexpress.RestExpress;
import org.restexpress.util.Environment;

public class Configuration
extends Environment
{
	private static final String DEFAULT_EXECUTOR_THREAD_POOL_SIZE = "20";

	private static final String PORT_PROPERTY = "port";
	private static final String DB_HOST_PROPERTY = "db_host";
	private static final String DB_PASSWD = "db_password";
	private static final String BASE_URL_PROPERTY = "base.url";
	private static final String EXECUTOR_THREAD_POOL_SIZE = "executor.threadPool.size";

	private int port;
	private String baseUrl;
	private String dbHost;
	private String dbPasswd;
	private int executorThreadPoolSize;

	private SampleController sampleController;
	private ArticleController articleController;
	private SourceController sourceController;

	@Override
	protected void fillValues(Properties p)
	{
		this.port = Integer.parseInt(p.getProperty(PORT_PROPERTY, String.valueOf(RestExpress.DEFAULT_PORT)));
		this.baseUrl = p.getProperty(BASE_URL_PROPERTY, "http://localhost:" + String.valueOf(port));
		this.dbHost = p.getProperty(DB_HOST_PROPERTY, "127.0.0.1");
		this.dbPasswd = p.getProperty(DB_PASSWD, "");
		this.executorThreadPoolSize = Integer.parseInt(p.getProperty(EXECUTOR_THREAD_POOL_SIZE, DEFAULT_EXECUTOR_THREAD_POOL_SIZE));
		initialize(dbHost, dbPasswd);
	}

	private void initialize(String dbhost, String pwd)
	{
		//sampleController = new SampleController();
		articleController = new ArticleController(dbhost, pwd);
		sourceController = new SourceController(dbhost, pwd);
	}
	
	public void setProperties (Properties p) {
		fillValues(p);
	}

	public int getPort()
	{
		return port;
	}
	
	public String getBaseUrl()
	{
		return baseUrl;
	}
	
	public int getExecutorThreadPoolSize()
	{
		return executorThreadPoolSize;
	}

	public SampleController getSampleController()
	{
		return sampleController;
	}

	public ArticleController getArticleController() {
		return articleController;
	}

	public SourceController getSourceController() {
		return sourceController;
	}

	public void setSourceController(SourceController sourceController) {
		sourceController = sourceController;
	}
}
