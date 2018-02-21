package com.example.rest.models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("counters")
public class Counter {
    @Id
    private String _id;

    private int seq;

    public Counter() {
    }

    public Counter(String _id) {
        this._id = _id;
        this.seq = 0;
    }

    public String get_id() {
        return _id;
    }

    public int getSeq() {
        return seq;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}