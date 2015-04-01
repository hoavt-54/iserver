package org.iiinews.android.model;

public class ResponseReadAllData {
	private ReadAllData data;
	private boolean isRefreshed;
	private long time;
	
	public ResponseReadAllData () {super();}

	public ReadAllData getData() {
		return data;
	}

	public void setData(ReadAllData data) {
		this.data = data;
	}

	public boolean isRefreshed() {
		return isRefreshed;
	}

	public void setRefreshed(boolean isRefreshed) {
		this.isRefreshed = isRefreshed;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
