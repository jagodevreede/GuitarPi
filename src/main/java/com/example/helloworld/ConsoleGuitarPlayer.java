package com.example.helloworld;

import java.util.HashSet;
import java.util.Set;

public class ConsoleGuitarPlayer extends GuitarPlayer {

    private long playTime = 0;
    private Set<GuitarNote> notesPlayed = new HashSet<>();

    @Override
    void prepareString(GuitarNote gn) {
    }

    @Override
    void playString(GuitarNote gn) {
        if (gn.isHit()) {
            System.out.println(gn.getName() + " on string " + gn.getStringNumber() + " on fred " + gn.getFred());
            notesPlayed.add(gn);
        }
    }

    @Override
    protected void waitMilliseconds(long waitTimeMS) {
        System.out.println("Next note in: " + waitTimeMS + "ms");
        playTime += waitTimeMS;
    }

    @Override
    public void close() throws Exception {
        System.out.println("Play time was: " + playTime);
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
