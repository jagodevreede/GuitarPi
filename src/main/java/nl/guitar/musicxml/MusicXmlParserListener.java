package nl.guitar.musicxml;

import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.GuitarPlayer;
import nl.guitar.player.strategy.ComplexStringStrategy;
import nl.guitar.player.strategy.HighStringStrategy;
import nl.guitar.player.strategy.StringStrategy;
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
    private final long parseStartTime = System.currentTimeMillis();
    private StringStrategy stringStrategy;

    public MusicXmlParserListener(GuitarPlayer guitarPlayer, GuitarTuning guitarTuning) {
        try {
            this.guitarTuning = guitarTuning;
            this.guitarPlayer = guitarPlayer;
            stringStrategy = new ComplexStringStrategy(guitarTuning);
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
            logger.debug("Parse done in {}ms", System.currentTimeMillis() - parseStartTime);
            guitarActions.add(guitarPlayer.calculateNotes(notes, guitarTuning, lastAction, stringStrategy));
            guitarPlayer.printStats(guitarActions);
            notes.clear();
            lastAction = null;
            System.gc();
            logger.info("Calculation done starting to play total calculation time {}ms", System.currentTimeMillis() - parseStartTime);
        } catch (Exception e) {
            logger.error("Failed to parse music xml", e);
            throw e;
        }
    }

    public List<GuitarAction> guitarActions() {
        return guitarActions;
    }

    @Override
    public void onNoteParsed(Note note) {
        logger.debug("Parsing note {}", note);
        try {
            if (!note.isHarmonicNote() && !notes.isEmpty()) {
                final GuitarAction action = guitarPlayer.calculateNotes(notes, guitarTuning, lastAction, stringStrategy);
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
