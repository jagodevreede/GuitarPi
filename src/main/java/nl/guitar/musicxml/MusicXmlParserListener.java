package nl.guitar.musicxml;

import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.GuitarPlayer;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MusicXmlParserListener extends ParserListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MusicXmlParserListener.class);

    private final GuitarPlayer guitarPlayer;
    private List<Note> notes = new ArrayList<>();
    private List<GuitarAction> guitarActions = new ArrayList<>();
    private long currentTimestamp = 0;

    public MusicXmlParserListener(GuitarPlayer guitarPlayer) {
        this.guitarPlayer = guitarPlayer;
        System.out.println("Pre calculation of notes started");
    }

    public void onTempoChanged(int tempoBPM) {
        guitarPlayer.setTempo(tempoBPM);
    }

    @Override
    public void onBarLineParsed(long id) {
        super.onBarLineParsed(id);
    }

    @Override
    public void onMarkerParsed(String marker) {
        super.onMarkerParsed(marker);
    }

    @Override
    public void afterParsingFinished() {
        guitarActions.add(guitarPlayer.calculateNotes(notes));
        notes.clear();
        System.gc();
        logger.info("Calculation done starting to play");
        guitarPlayer.playActions(guitarActions);
        guitarActions.clear();
        guitarPlayer.resetFreds();
        logger.info("Done playing");
    }

    @Override
    public void onNoteParsed(Note note) {
        if (!note.isHarmonicNote() && !notes.isEmpty()) {
            final GuitarAction action = guitarPlayer.calculateNotes(notes);
            action.timeStamp = currentTimestamp;
            currentTimestamp += action.timeTillNextNote;
            guitarActions.add(action);
            notes.clear();
        }
        notes.add(note);
    }

}
