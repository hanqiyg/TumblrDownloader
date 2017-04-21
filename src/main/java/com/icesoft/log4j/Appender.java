package com.icesoft.log4j;

import java.util.ArrayList;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class Appender  extends AppenderSkeleton {
    ArrayList<LoggingEvent> eventsList = new ArrayList<LoggingEvent>();

    @Override
    protected void append(LoggingEvent event) {
    	 String message = null;
         if(event.locationInformationExists()){
             StringBuilder formatedMessage = new StringBuilder();
             formatedMessage.append(event.getLocationInformation().getClassName());
             formatedMessage.append(".");
             formatedMessage.append(event.getLocationInformation().getMethodName());
             formatedMessage.append(":");
             formatedMessage.append(event.getLocationInformation().getLineNumber());
             formatedMessage.append(" - ");
             formatedMessage.append(event.getMessage().toString());
             message = formatedMessage.toString();
         }else{
             message = event.getMessage().toString();
         }
         System.out.println(message);

    }

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }

}
