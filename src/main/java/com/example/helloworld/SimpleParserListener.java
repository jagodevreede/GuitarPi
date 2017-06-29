package com.example.helloworld;

import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.List;

public class SimpleParserListener extends ParserListenerAdapter {

    private final GuitarPlayer guitarPlayer;
    private List<Note> notes = new ArrayList<>();

    public SimpleParserListener(GuitarPlayer guitarPlayer) {
        this.guitarPlayer = guitarPlayer;
    }

    public void onTempoChanged(int tempoBPM) {
        guitarPlayer.setTempo(tempoBPM);
    }

    @Override
    public void afterParsingFinished() {
        guitarPlayer.playNotes(notes);
        notes.clear();
    }

    @Override
    public void onNoteParsed(Note note) {
        if (!note.isHarmonicNote() && !notes.isEmpty()) {
            guitarPlayer.playNotes(notes);
            notes.clear();
        }
        notes.add(note);
    }

}
