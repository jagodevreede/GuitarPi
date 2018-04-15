package nl.guitar.player;

import nl.guitar.StatusWebsocket;
import nl.guitar.controlers.Controller;
import nl.guitar.data.ConfigRepository;
import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.object.GuitarNote;
import org.jfugue.theory.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GuitarPlayer implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(GuitarPlayer.class);
    public static final int PREPARE_TIME = 40;
    public static final int MINIMUM_MS_BETWEEN_NOTES = 40;
    private static final int MAX_FRED_NUMBER = 16; // was 8
    protected final Controller controller;
    private int notesBarsPlayed;
    private float tempo = 80;
    private boolean isStopped = false;

    public GuitarPlayer(Controller controller) {
        this.controller = controller;
    }

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
                if (gn.getFred() > MAX_FRED_NUMBER) {
                    throw new IllegalStateException("Unable to play fred " + gn.getFred() + " on string "+ gn.getStringNumber() + " note:" + note.getValue() + " on " + notesBarsPlayed);
                }
                notesToPlay.add(gn);
                logger.debug("Duration = " + note.getDuration());
                logger.debug("har " + note.isHarmonicNote());
                logger.debug("mel " + note.isMelodicNote());
            }
            long timeout = (long) (60f / tempo * 4f * note.getDuration() * 1000);
            if (timeout < shortestNote) {
                shortestNote = timeout;
                if (shortestNote < MINIMUM_MS_BETWEEN_NOTES) {
                    throw new IllegalStateException("Notes to fast minimum time between notes " + MINIMUM_MS_BETWEEN_NOTES + " but wanted " + shortestNote);
                }
            }
        }
        List<Integer> distinctStrings = notesToPlay.stream().map(GuitarNote::getStringNumber).distinct().collect(Collectors.toList());
        if (notesToPlay.size() > distinctStrings.size()) {
            logger.error("Want to play a sting multiple times on note: " + notesBarsPlayed);
            notesToPlay.forEach((n) -> logger.error(n.toString()));
            throw new IllegalStateException("Want to play a sting multiple times on note: " + notesBarsPlayed);
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

    public void printStats(List<GuitarAction> actions) {
        long shortestNote = actions.stream()
                .map((GuitarAction a) -> a.timeTillNextNote)
                .min(Long::compare)
                .get();
        final List<Integer> notes = actions.stream()
                .flatMap((GuitarAction a) -> a.notesToPlay.stream()
                        .map(GuitarNote::getNoteValue)
                ).filter(i -> i > 0)
                .collect(Collectors.toList());
        long lowestNote = notes.stream()
                .min(Integer::compare)
                .get();
        long highestNote = notes.stream()
                .max(Integer::compare)
                .get();
        logger.info("Number statistics:");
        logger.info("Shortest note {}ms", shortestNote);
        logger.info("Lowest note {}", lowestNote);
        logger.info("Highest note {}", highestNote);
    }

    private void playNotes(List<GuitarNote> notesToPlay){
        notesBarsPlayed++;
        notesToPlay.forEach(this::prepareString);
        controller.waitMilliseconds(PREPARE_TIME);

        notesToPlay.forEach(this::playString);
    }

    public void playActions(List<GuitarAction> guitarActions) {
        isStopped = false;
        StatusWebsocket.sendToAll("start");
        controller.start();
        for (GuitarAction action : guitarActions) {
            controller.waitUntilTimestamp(action.timeStamp);
            StatusWebsocket.sendToAll("next");
            playNotes(action.notesToPlay);
            if (isStopped) {
                break;
            }
        }
        StatusWebsocket.sendToAll("stop");
    }

    abstract public void resetFreds();

    public void setTempo(int tempo) {
        logger.info("Tempo changed to = " + tempo);
        this.tempo = tempo;
    }

    public void stop() {
        this.isStopped = true;
    }


}
