package web_service.backend;

import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.setPort;

import web_service.util.Util;


public class Server {

    public static void main(String[] args) {
        setPort(Util.getWebPort());
        externalStaticFileLocation("src/main/webapp");
        final Service service = new Service(Util.getNeo4jUrl());
        new Routes(service).init();
    }
}