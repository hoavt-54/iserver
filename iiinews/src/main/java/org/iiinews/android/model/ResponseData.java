package org.iiinews.android.model;

import java.util.ArrayList;
import java.util.List;

public class ResponseData {
	
	private List<? extends Object> data;
	
	private Paging paging;
	
	private boolean isRefreshed;
	
	public ResponseData () {
		data = new ArrayList<>();
		paging = new Paging();
	}
	
	public ResponseData (List<? extends Object> data, Paging paging, boolean  isRefreshed) {
		this.data = data;
		this.paging= paging;
		this.isRefreshed = isRefreshed;
	}

	public List<? extends Object> getData() {
		return data;
	}

	public void setData(List<? extends Object> data) {
		this.data = data;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public boolean isRefreshed() {
		return isRefreshed;
	}

	public void setRefreshed(boolean isRefreshed) {
		this.isRefreshed = isRefreshed;
	}
	
}
