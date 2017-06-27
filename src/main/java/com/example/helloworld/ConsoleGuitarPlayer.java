package com.example.helloworld;

public class ConsoleGuitarPlayer extends GuitarPlayer {
    @Override
    void playString(short stringNumber) {
        System.out.println("Playing string " + stringNumber);
    }

    @Override
    public void close() throws Exception {

    }
}
