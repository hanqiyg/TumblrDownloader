### List
-[x] Item 1
-[ ] Item 2
  -[x] Item 2.1
  -[ ] Item 2.2
-[x] Item 3
 
 ### Log4j Config
 ```java
 static 
 {
		Properties pro = new Properties();
		pro.put("log4j.rootLogger", "debug,stdout,my,R,A");

		pro.put("log4j.appender.my", "com.icesoft.log4j.Appender");
		pro.put("log4j.appender.my.layout", "org.apache.log4j.PatternLayout");
		pro.put("log4j.appender.my.Threshold", "INFO");
		pro.put("log4j.appender.my.layout.ConversionPattern", "[%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} method:%l%n%m%n");

		pro.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
		pro.put("log4j.appender.stdout.Target","System.out");
		pro.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
		pro.put("log4j.appender.stdout.layout.ConversionPattern", "[%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} method:%l%n%m%n");
		
		pro.put("log4j.appender.R", "org.apache.log4j.RollingFileAppender");
		pro.put("log4j.appender.R.File", SettingService.getInstance().getPath() + File.separator + "logs" + File.separator + "Info.log");
		pro.put("log4j.appender.R.MaxFileSize", "10000KB");
		pro.put("log4j.appender.R.MaxBackupIndex", "20");
		pro.put("log4j.appender.R.Threshold", "INFO");
		pro.put("log4j.appender.R.layout", "org.apache.log4j.PatternLayout");
		pro.put("log4j.appender.R.layout.ConversionPattern", "%-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n");

		pro.put("log4j.appender.A", "org.apache.log4j.RollingFileAppender");
		pro.put("log4j.appender.A.File", SettingService.getInstance().getPath() + File.separator + "logs" + File.separator + "Error.log");
		pro.put("log4j.appender.A.MaxFileSize", "10000KB");
		pro.put("log4j.appender.A.MaxBackupIndex", "20");
		pro.put("log4j.appender.A.Threshold", "ERROR");
		pro.put("log4j.appender.A.layout", "org.apache.log4j.PatternLayout");
		pro.put("log4j.appender.A.layout.ConversionPattern", "%n[%d{HH:mm:ss}] [%p] %m");

		PropertyConfigurator.configure(pro);
 }
 
 ### 甘特图
 ```
 graph TD
 A[Start] --> B(Y/N)
 B-->|Yes|C(Go on)
 B-->|No| D(Break)
 C-->A
 D-->E(End)
