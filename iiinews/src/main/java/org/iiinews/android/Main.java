package org.iiinews.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.iiinews.android.config.Configuration;
import org.iiinews.android.serialization.SerializationProvider;
import org.restexpress.RestExpress;
import org.restexpress.pipeline.SimpleConsoleLogMessageObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whirlycott.cache.CacheManager;

public class Main
{
	private static final String SERVICE_NAME = "IIIN server";
	private static final Logger LOG = LoggerFactory.getLogger(SERVICE_NAME);

	public static void main(String[] args) throws Exception
	{
		RestExpress server = initializeServer(args);
		server.awaitShutdown();
		CacheManager.getInstance().shutdown();
	}

	public static RestExpress initializeServer(String[] args) throws IOException
	{
		RestExpress.setSerializationProvider(new SerializationProvider());

		Configuration config = loadEnvironment(args);
		RestExpress server = new RestExpress()
				.setName(SERVICE_NAME)
				.setBaseUrl(config.getBaseUrl())
				.setExecutorThreadCount(config.getExecutorThreadPoolSize())
				.addMessageObserver(new SimpleConsoleLogMessageObserver());

		Routes.define(config, server);
		server.bind(config.getPort());
		return server;
    }

	private static Configuration loadEnvironment(String[] args)
    throws FileNotFoundException, IOException
    {
		java.util.Properties prop = new Properties();
	    if (args.length >= 3)
		{
			prop.put("db_host", args[0]);
			prop.put("port", args[1]);
			prop.put("db_password", args[2]);
			
		}else if (args.length >=2 ){
			prop.put("db_host", args[0]);
			prop.put("port", args[1]);
			prop.put("db_password", "");
		}
	    else {
		    prop.put("port", "8081");
		    prop.put("db_host", "127.0.0.1");
		    prop.put("db_password", "");
	    }
	    Configuration config = new Configuration();
	    config.setProperties(prop);
	    return config;
    }
}
