package com.example.helloworld;

import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Note;

public class SimpleParserListener extends ParserListenerAdapter {

    private final GuitarPlayer guitarPlayer;

    public SimpleParserListener(GuitarPlayer guitarPlayer) {
        this.guitarPlayer = guitarPlayer;
    }

    public void onTempoChanged(int tempoBPM) {
        guitarPlayer.setTempo(tempoBPM);
    }

    @Override
    public void beforeParsingStarts() {
        System.out.println("Start parsing");
    }

    @Override
    public void afterParsingFinished() {
        System.out.println("Done parsing");
    }

    @Override
    public void onNotePressed(Note note) {
        System.out.println("Note press: " + note);

    }

    @Override
    public void onNoteReleased(Note note) {
        System.out.println("Note release: " + note);
    }

    @Override
    public void onNoteParsed(Note note) {
        if (note.isRest()) {
            return;
        }
        System.out.println("Note parsed: " + note);
        guitarPlayer.playNote(note);
    }

}
