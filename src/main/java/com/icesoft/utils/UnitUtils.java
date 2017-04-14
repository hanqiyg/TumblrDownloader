package com.icesoft.utils;

import java.math.BigDecimal;

public class UnitUtils {
	public static String getFormatSpeed(double size) {  
        double kiloByte = size/1024;  
        if(kiloByte < 1) {  
            return size + "B/s";  
        }  
          
        double megaByte = kiloByte/1024;  
        if(megaByte < 1) {  
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));  
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "kB/s";  
        }  
          
        double gigaByte = megaByte/1024;  
        if(gigaByte < 1) {  
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));  
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB/s";  
        }  
          
        double teraBytes = gigaByte/1024;  
        if(teraBytes < 1) {  
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));  
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB/s";  
        }  
        BigDecimal result4 = new BigDecimal(teraBytes);  
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB/s";  
    }
	public static String getFormatSize(double size) {  
        double kiloByte = size/1024;  
        if(kiloByte < 1) {  
            return size + "B";  
        }  
          
        double megaByte = kiloByte/1024;  
        if(megaByte < 1) {  
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));  
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "kB";  
        }  
          
        double gigaByte = megaByte/1024;  
        if(gigaByte < 1) {  
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));  
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";  
        }  
          
        double teraBytes = gigaByte/1024;  
        if(teraBytes < 1) {  
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));  
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";  
        }  
        BigDecimal result4 = new BigDecimal(teraBytes);  
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";  
    }
	public static String getDays(long time) {
		String format = "%3d Days %02d:%02d:%02d.%03d";
		long days=0,hours=0,mins=0,seconds=0,ms=0;
		ms = time%1000l;
		seconds = time / 1000l;
		if(seconds < 60l )
		{
			return String.format(format,days,hours,mins,seconds,ms);
		}
		mins = seconds / 60l ;
		seconds = seconds % 60l ;
		if(mins < 60 )
		{
			return String.format(format,days,hours,mins,seconds,ms);
		}
		hours = mins / 60l ;
		mins = mins % 60l ;
		if(hours < 24 )
		{
			return String.format(format,days,hours,mins,seconds,ms);
		}
		days = hours / 24l ;
		hours = hours % 24l ;
		return String.format(format,days,hours,mins,seconds,ms);
    }
}
