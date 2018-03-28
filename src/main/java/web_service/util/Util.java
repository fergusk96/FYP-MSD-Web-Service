package web_service.util;

/* This file determines both the neo4j URL and the port of the web service */

public class Util {
    public static final String DEFAULT_URL = "http://127.0.0.1:7474";
    public static int getWebPort() {
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            return 8080;
        }
        return Integer.parseInt(webPort);
    }

    public static String getNeo4jUrl() {
      
        return DEFAULT_URL;
  
}
}
