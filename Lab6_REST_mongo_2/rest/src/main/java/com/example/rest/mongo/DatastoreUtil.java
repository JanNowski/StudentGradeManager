package com.example.rest.mongo;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;


public class DatastoreUtil {
    private static DatastoreUtil Instance = new DatastoreUtil();
    private org.mongodb.morphia.Datastore datastore;

    public static DatastoreUtil getInstance() {
        return Instance;
    }

    private DatastoreUtil() {
        final Morphia morphia = new Morphia();
        datastore = morphia.createDatastore(new MongoClient("localhost", 8004), "rest");
        morphia.mapPackage("com.example.rest.models");
        datastore.ensureIndexes();
    }

    public Datastore getDatastore() {
        return datastore;
    }
}
