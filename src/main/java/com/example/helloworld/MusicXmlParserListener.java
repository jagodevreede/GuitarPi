package com.example.helloworld;

import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Note;

import java.util.ArrayList;
import java.util.List;

public class MusicXmlParserListener extends ParserListenerAdapter {

    private final GuitarPlayer guitarPlayer;
    private List<Note> notes = new ArrayList<>();
    private List<GuitarAction> guitarActions = new ArrayList<>();

    public MusicXmlParserListener(GuitarPlayer guitarPlayer) {
        this.guitarPlayer = guitarPlayer;
        System.out.println("Pre calculation of notes started");
    }

    public void onTempoChanged(int tempoBPM) {
        guitarPlayer.setTempo(tempoBPM);
    }

    @Override
    public void afterParsingFinished() {
        guitarActions.add(guitarPlayer.calculateNotes(notes));
        notes.clear();
        System.gc();
        System.out.println("Calculation done starting to play");
        guitarPlayer.playActions(guitarActions);
    }

    @Override
    public void onNoteParsed(Note note) {
        if (!note.isHarmonicNote() && !notes.isEmpty()) {
            guitarActions.add(guitarPlayer.calculateNotes(notes));
            notes.clear();
        }
        notes.add(note);
    }

}
