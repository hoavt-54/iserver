package org.iiinews.android.model;

public class Paging {
	
	private int nextOffset;
	
	private int itemLeft;
	
	public Paging (){}
	
	public Paging (int nextOffset, int itemLeft){
		this.itemLeft = itemLeft;
		this.setNextOffset(nextOffset);
	}


	public int getItemLeft() {
		return itemLeft;
	}

	public void setItemLeft(int itemLeft) {
		this.itemLeft = itemLeft;
	}

	public int getNextOffset() {
		return nextOffset;
	}

	public void setNextOffset(int nextOffset) {
		this.nextOffset = nextOffset;
	}
	
	
}
