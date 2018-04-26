package nl.guitar.musicxml;

import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.GuitarPlayer;
import nl.guitar.player.tuning.GuitarTuning;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static nl.guitar.player.GuitarPlayer.MINIMUM_MS_BETWEEN_NOTES;

public class MusicXmlParserListener extends ParserListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MusicXmlParserListener.class);

    private final GuitarPlayer guitarPlayer;
    private List<Note> notes = new ArrayList<>();
    private List<GuitarAction> guitarActions = new ArrayList<>();
    private long currentTimestamp = 0;
    private final GuitarTuning guitarTuning;
    private GuitarAction lastAction;

    public MusicXmlParserListener(GuitarPlayer guitarPlayer, GuitarTuning guitarTuning) {
        try {
            this.guitarTuning = guitarTuning;
            this.guitarPlayer = guitarPlayer;
            logger.info("Pre calculation of notes started");
        } catch (Exception e) {
            logger.error("Failed to parse music xml", e);
            throw e;
        }
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
        try {
            guitarActions.add(guitarPlayer.calculateNotes(notes, guitarTuning, lastAction));
            guitarPlayer.printStats(guitarActions);
            notes.clear();
            lastAction = null;
            System.gc();
            logger.info("Calculation done starting to play");
            guitarPlayer.playActions(guitarActions);
            guitarActions.clear();
            guitarPlayer.resetFreds();
            logger.info("Done playing");
        } catch (Exception e) {
            logger.error("Failed to parse music xml", e);
            throw e;
        }
    }

    @Override
    public void onNoteParsed(Note note) {
        logger.debug("Parsing note {}", note);
        try {
            if (!note.isHarmonicNote() && !notes.isEmpty()) {
                final GuitarAction action = guitarPlayer.calculateNotes(notes, guitarTuning, lastAction);
                action.timeStamp = currentTimestamp;
                currentTimestamp += action.timeTillNextNote;
                guitarActions.add(action);
                lastAction = action;
                notes.clear();
            }
            notes.add(note);
        } catch (Exception e) {
            logger.error("Failed to parse music xml", e);
            throw e;
        }
    }

}
