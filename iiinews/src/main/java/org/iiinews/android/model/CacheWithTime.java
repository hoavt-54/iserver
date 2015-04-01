package org.iiinews.android.model;

public class CacheWithTime {
	private Object cachedObject;
	private long cacheTime;
	
	public CacheWithTime (){
		
	}
	
	public CacheWithTime (Object cachedObject, long cacheTime){
		this.setCachedObject(cachedObject);
		this.setCacheTime(cacheTime);
	}

	public Object getCachedObject() {
		return cachedObject;
	}

	public void setCachedObject(Object cachedObject) {
		this.cachedObject = cachedObject;
	}

	public long getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}
}
