package com.example.rest;

import java.util.Random;

public abstract class IndexGenerator {
    /*public static String generate() {
        return UUID.randomUUID().toString().replace('-', 'a');
    }*/
    public static String createRandomIndex()
    {
        // numbers (6), random 0-9
        Random r = new Random();
        long numbers = 100000 + (long)(r.nextFloat() * 899900);

        return String.valueOf(numbers);
    }
}