package com.example.rest.resources;

import com.example.rest.models.Counter;
import com.example.rest.mongo.DatastoreUtil;
import org.mongodb.morphia.Datastore;

public enum CounterResource {
    instance;

    private Datastore datastore;

    private CounterResource() {
        this.datastore = DatastoreUtil.getInstance().getDatastore();
    }

    public int getSeq(String name) {
        Counter counter = this.datastore.findAndModify(this.datastore.createQuery(Counter.class).filter("_id", name), this.datastore.createUpdateOperations(Counter.class).inc("seq"));

        return counter.getSeq();
    }
}