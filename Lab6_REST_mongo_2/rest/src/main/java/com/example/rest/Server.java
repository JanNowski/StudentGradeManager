package com.example.rest;

import com.example.rest.mongo.CustomHeaders;
import com.example.rest.mongo.DefaultData;
import com.example.rest.resources.DateParamConverterProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;


public class Server {
    public static void main(String[] args) throws IOException {
        try {
            DefaultData.initializeData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        ResourceConfig config = new ResourceConfig().packages("com.example.rest.resources").register(DeclarativeLinkingFeature.class);
        config.register(new CustomHeaders());
        config.register(new DateParamConverterProvider("yyyy-MM-dd"));
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
        server.start();
    }
}
