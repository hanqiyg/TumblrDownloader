package com.icesoft.tumblr.state;

public enum DownloadPriority

{	
	CRITICAL,
	HIGH,
	MEDIUM,
	NORMAL,
	LOW;
/*	LOW,
	NORMAL,
	MEDIUM,
	HIGH,
	CRITICAL;*/
  public static DownloadPriority valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return values()[ordinal];
    }
}
