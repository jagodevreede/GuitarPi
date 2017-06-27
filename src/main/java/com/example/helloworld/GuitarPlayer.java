package com.example.helloworld;

import org.jfugue.theory.Note;

import java.util.concurrent.TimeUnit;

public abstract class GuitarPlayer implements AutoCloseable {
    private int tempo = 80;

    abstract void playString(short stringNumber);

    public void playNote(Note note){
        GuitarNote gn = new GuitarNote(note);
        playString(gn.getStringNumber());
        System.out.println("Duration = " + note.getDuration());
        try {
            long timeout = (long) (tempo / 60 * 4 * note.getDuration() * 1000);
            System.out.println("Timeout : " + timeout);
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTempo(int tempo) {
        System.out.println("Tempo changed to = " + tempo);
        this.tempo = tempo;
    }
}
