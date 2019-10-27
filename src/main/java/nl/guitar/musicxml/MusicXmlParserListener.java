package nl.guitar.musicxml;

import nl.guitar.player.GuitarPlayer;
import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.object.GuitarNote;
import nl.guitar.player.strategy.ComplexStringStrategy;
import nl.guitar.player.strategy.HighStringStrategy;
import nl.guitar.player.strategy.LowStringStrategy;
import nl.guitar.player.strategy.StringStrategy;
import nl.guitar.player.tuning.GuitarTuning;
import org.jfugue.parser.Parser;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicXmlParserListener extends ParserListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MusicXmlParserListener.class);

    private final GuitarPlayer guitarPlayer;
    private List<Note> notes = new ArrayList<>();
    private List<GuitarAction> guitarActions = new ArrayList<>();
    private long currentTimestamp = 0;
    private final GuitarTuning guitarTuning;
    private GuitarAction lastAction;
    private final long parseStartTime = System.currentTimeMillis();
    private final List<StringStrategy> stringStrategies;

    public MusicXmlParserListener(GuitarPlayer guitarPlayer, GuitarTuning guitarTuning) {
        try {
            this.guitarTuning = guitarTuning;
            this.guitarPlayer = guitarPlayer;
            stringStrategies = Arrays.asList(new ComplexStringStrategy(guitarTuning), new HighStringStrategy(), new LowStringStrategy());
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
            GuitarAction bestAction = getBestAction();
            if (!bestAction.notesToPlay.isEmpty()) {
                guitarActions.add(bestAction);
            }
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

    private GuitarAction getBestAction() {
        GuitarAction bestAction = null;
        for (StringStrategy stringStrategy : stringStrategies) {
            GuitarAction action = guitarPlayer.calculateNotes(notes, guitarTuning, lastAction, stringStrategy);
            if (action.error == null) {
                return action;
            }
            if (bestAction == null) {
                bestAction = action;
            } else if (getHitCount(bestAction) < getHitCount(action)) {
                bestAction = action;
            }
        }
        return bestAction;
    }

    private static long getHitCount(GuitarAction action) {
        return action.notesToPlay.stream().filter(GuitarNote::isHit).count();
    }

    public List<GuitarAction> guitarActions() {
        return guitarActions;
    }

    @Override
    public void onNoteParsed(Note note) {
        logger.debug("Parsing note {}", note);
        try {
            if (!note.isHarmonicNote() && !notes.isEmpty()) {
                final GuitarAction action = getBestAction();
                action.timeStamp = currentTimestamp;
                currentTimestamp += action.timeTillNextNote;
                if (!action.notesToPlay.isEmpty()) {
                    guitarActions.add(action);
                }
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
