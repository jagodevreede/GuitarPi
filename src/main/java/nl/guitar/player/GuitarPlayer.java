package nl.guitar.player;

import nl.guitar.StatusWebsocket;
import nl.guitar.controlers.Controller;
import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.object.GuitarNote;
import org.jfugue.theory.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class GuitarPlayer implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(GuitarPlayer.class);
    public static final int PREPARE_TIME = 40;
    public static final int MINIMUM_MS_BETWEEN_NOTES = 80;
    protected final Controller controller;
    private int notesBarsPlayed;
    private float tempo = 80;
    private boolean isStopped = false;
    private long startTime;

    public GuitarPlayer(Controller controller) {
        this.controller = controller;
    }

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
            notesToPlay.forEach((n) -> logger.error(n.toString()));
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
        isStopped = false;
        StatusWebsocket.sendToAll("start");
        startTime = System.currentTimeMillis();
        for (GuitarAction action : guitarActions) {
            waitUntilTimestamp(action.timeStamp);
            StatusWebsocket.sendToAll("next");
            playNotes(action.notesToPlay);
            if (isStopped) {
                break;
            }
        }
        StatusWebsocket.sendToAll("stop");
    }

    abstract public void resetFreds();

    protected void waitUntilTimestamp(long timeStamp) {
        final long nextTimeStampInTime = startTime + timeStamp;
        while (nextTimeStampInTime - System.currentTimeMillis() > 250) {
            waitMilliseconds(100);
        }
        while (nextTimeStampInTime - System.currentTimeMillis() > 0) {
            // No op
        }
    }

    protected void waitMilliseconds(long waitTimeMS) {
        try {
            TimeUnit.MILLISECONDS.sleep(waitTimeMS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTempo(int tempo) {
        logger.info("Tempo changed to = " + tempo);
        this.tempo = tempo;
    }

    public void stop() {
        this.isStopped = true;
    }


}
