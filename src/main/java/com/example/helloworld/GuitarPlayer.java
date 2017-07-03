package com.example.helloworld;

import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GuitarPlayer implements AutoCloseable {
    public static final int PREPARE_TIME = 40;
    private int notesBarsPlayed;
    private float tempo = 80;

    /** return the number of ms to wait for the note */
    abstract void prepareString(GuitarNote gn);

    abstract void playString(GuitarNote gn);

    public GuitarAction calculateNotes(List<Note> notes) {
        notesBarsPlayed++;
        GuitarAction action = new GuitarAction();
        long shortestNote = Long.MAX_VALUE;
        List<GuitarNote> notesToPlay = new ArrayList<>(notes.size());
        for (Note note : notes) {
            resetFreds();
            if (!note.isRest()) {
                GuitarNote gn = new GuitarNote(note);
                if (gn.getFred() > 8) {
                    throw new IllegalStateException("Unable to play fred " + gn.getFred() + " on string "+ gn.getStringNumber() + " note:" + note.getValue() + " on " + notesBarsPlayed);
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
        List<Integer> distinctStrings = notesToPlay.stream().map(gn -> gn.getStringNumber()).distinct().collect(Collectors.toList());
        if (notesToPlay.size() > distinctStrings.size()) {
            notesToPlay.forEach(System.err::println);
            System.err.println("");
            throw new IllegalStateException("Want to play a sing multiple times on note: " + notesBarsPlayed);
        }

        for (int i =0; i < 6; i++) {
            if (!distinctStrings.contains(i)) {
                notesToPlay.add(new GuitarNote(i, 0, false));
            }
        }

        action.timeTillNextNote = shortestNote;
        action.notesToPlay = notesToPlay;
        return action;
    }

    private void playNotes(List<GuitarNote> notesToPlay){
        notesBarsPlayed++;
        notesToPlay.forEach(this::prepareString);
        waitMilliseconds(PREPARE_TIME);

        notesToPlay.forEach(this::playString);
    }

    public void playActions(List<GuitarAction> guitarActions) {
        for (GuitarAction action : guitarActions) {
            playNotes(action.notesToPlay);
            waitMilliseconds(action.timeTillNextNote - PREPARE_TIME);
        }
    }

    protected void resetFreds() {

    }

    protected void waitMilliseconds(long waitTimeMS) {
        try {
            System.out.println("Next note in: " + waitTimeMS + "ms");
            TimeUnit.MILLISECONDS.sleep(waitTimeMS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTempo(int tempo) {
        System.out.println("Tempo changed to = " + tempo);
        this.tempo = tempo;
    }


}
