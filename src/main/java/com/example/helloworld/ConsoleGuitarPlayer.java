package com.example.helloworld;

import java.util.HashSet;
import java.util.Set;

public class ConsoleGuitarPlayer extends GuitarPlayer {

    Set<GuitarNote> notesPlayed = new HashSet<>();

    @Override
    int prepareString(GuitarNote gn) {
        return 0;
    }

    @Override
    void playString(GuitarNote gn) {
        System.out.println("Playing string " + gn.getStringNumber());
        System.out.println("Playing fred " + gn.getFred());
        notesPlayed.add(gn);
    }

    @Override
    protected void waitMilliseconds(long shortestNote) {
        System.out.println("Next note in: " + shortestNote + "ms");
    }

    @Override
    public void close() throws Exception {
        System.out.println("Played the following notes:");
        notesPlayed.stream().sorted((gn1, gn2) -> {
            if (gn1.getStringNumber() == gn2.getStringNumber()) {
                return Integer.compare(gn1.getFred(), gn2.getFred());
            }
            return Integer.compare(gn1.getStringNumber(), gn2.getStringNumber());
        }).forEach(gn -> {
            System.out.println("String: " + gn.getStringNumber() + " fred: " + gn.getFred());
        });
    }
}
