package com.example.helloworld;

import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class GuitarPlayer implements AutoCloseable {
    private float tempo = 80;

    /** return the number of ms to wait for the note */
    abstract int prepareString(GuitarNote gn);

    abstract void playString(GuitarNote gn);

    public void playNotes(List<Note> notes){
        long shortestNote = Long.MAX_VALUE;
        List<GuitarNote> notesToPlay = new ArrayList<>(notes.size());
        for (Note note : notes) {
            resetFreds();
            if (!note.isRest()) {
                GuitarNote gn = new GuitarNote(note);
                if (gn.getFred() > 4) {
                    throw new IllegalStateException("Unable to play fred " + gn.getFred() + " on string "+ gn.getStringNumber());
                }
                notesToPlay.add(gn);
                System.out.println("Duration = " + note.getDuration());
                System.out.println("har " + note.isHarmonicNote());
                System.out.println("mel " + note.isMelodicNote());
            }
            long timeout = (long) (60f / tempo * 4f * note.getDuration() * 1000);
            if (timeout < shortestNote) {
                shortestNote = timeout;
            }
        }
        if (notesToPlay.size() > notesToPlay.stream().map(gn -> gn.getStringNumber()).distinct().count()) {
            throw new IllegalStateException("Want to play a sing multiple times");
        }

        int max = notesToPlay.stream().mapToInt(this::prepareString).max().orElse(0);
        if (max > 0) {
            waitMilliseconds(max);
        }

        notesToPlay.forEach(this::playString);
        waitMilliseconds(shortestNote - max);
    }

    protected void resetFreds() {

    }

    protected void waitMilliseconds(long shortestNote) {
        try {
            System.out.println("Next note in: " + shortestNote + "ms");
            TimeUnit.MILLISECONDS.sleep(shortestNote);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTempo(int tempo) {
        System.out.println("Tempo changed to = " + tempo);
        this.tempo = tempo;
    }
}
