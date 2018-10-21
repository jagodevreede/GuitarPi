package nl.guitar.player;

import nl.guitar.StatusWebsocket;
import nl.guitar.controlers.Controller;
import nl.guitar.data.ConfigRepository;
import nl.guitar.domain.PlectrumConfig;
import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.object.GuitarNote;
import nl.guitar.player.object.NoteComparator;
import nl.guitar.player.tuning.GuitarTuning;
import org.jfugue.theory.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GuitarPlayer implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(GuitarPlayer.class);
    public static final int PREPARE_TIME = 150;
    public static final int MINIMUM_MS_BETWEEN_NOTES = 150;
    protected final Controller controller;
    protected final ConfigRepository configRepository;
    private int notesBarsPlayed;
    private float tempo = 60;
    private volatile boolean isStopped = false;
    private List<GuitarAction> lastPlayedActions;

    public GuitarPlayer(Controller controller, ConfigRepository configRepository) {
        this.controller = controller;
        this.configRepository = configRepository;
    }

    abstract void prepareString1(GuitarNote gn);
    abstract void prepareString2(GuitarNote gn);
    abstract void prepareString3(GuitarNote gn);

    abstract void playString(GuitarNote gn);

    public GuitarAction calculateNotes(List<Note> notes, GuitarTuning guitarTuning, GuitarAction lastAction) {
        GuitarAction action = new GuitarAction();
        if (lastAction == null) {
            action.instructionNumber = 0;
        } else {
            action.instructionNumber = lastAction.instructionNumber + 1;
        }
        try {
            notes.sort(NoteComparator.INSTANCE);
            notesBarsPlayed++;
            long shortestNote = Long.MAX_VALUE;
            List<GuitarNote> notesToPlay = new ArrayList<>(notes.size());
            int[] stringsTaken = new int[] {-1, -1, -1 ,-1, -1, -1};
            if (lastAction != null && lastAction.timeTillNextNote < PREPARE_TIME) {
                lastAction.notesToPlay.stream().filter(GuitarNote::isHit).forEach(n -> stringsTaken[n.getStringNumber()] = n.getNoteValue());
            }
            for (Note note : notes) {
                resetFreds();
                if (!note.isRest()) {
                    GuitarNote gn = new GuitarNote(note, guitarTuning, stringsTaken);
                    notesToPlay.add(gn);
                    stringsTaken[gn.getStringNumber()] = gn.getNoteValue();
                    logger.debug("Duration = {}", note.getDuration());
                    //logger.debug("har " + note.isHarmonicNote());
                    //logger.debug("mel " + note.isMelodicNote());
                }
                long timeout = (long) (60f / tempo * 4f * note.getDuration() * 1000);
                if (timeout < shortestNote) {
                    shortestNote = timeout;
                }
            }
            action.timeTillNextNote = shortestNote;

            List<Short> distinctStrings = notesToPlay.stream().map(GuitarNote::getStringNumber).distinct().collect(Collectors.toList());
            if (notesToPlay.size() > distinctStrings.size()) {
                logger.error("Want to play a sting multiple times on note: " + notesBarsPlayed + " on string " + distinctStrings + " time since last note:" + (lastAction != null ? lastAction.timeTillNextNote : -10));
                notesToPlay.forEach((n) -> logger.error(n.toString()));
                throw new IllegalStateException("Want to play a sting multiple times on note: " + notesBarsPlayed + " on string " + distinctStrings);
            }

            for (short i = 0; i < 6; i++) {
                if (!distinctStrings.contains(i)) {
                    notesToPlay.add(new GuitarNote(i, 0, false));
                }
            }
            action.notesToPlay = notesToPlay;
        } catch (Exception e) {
            logger.error("Note calculation error for note ("+ action.instructionNumber +"): " + e.getMessage());
            action.error = e.getMessage();
        }
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
        notesToPlay.forEach(this::prepareString1);
        controller.waitMilliseconds(PREPARE_TIME /3);
        notesToPlay.forEach(this::prepareString2);
        controller.waitMilliseconds(PREPARE_TIME /3);
        notesToPlay.forEach(this::prepareString3);
        controller.waitMilliseconds(PREPARE_TIME /3);

        notesToPlay.forEach(this::playString);
    }

    public void playActions(List<GuitarAction> guitarActions) {
        this.lastPlayedActions = new ArrayList<>(guitarActions);
        initStrings();
        isStopped = false;
        StatusWebsocket.sendToAll("start");
        controller.start(PREPARE_TIME);
        for (GuitarAction action : guitarActions) {
            controller.waitUntilTimestamp(action.timeStamp - PREPARE_TIME);
            if (isStopped) {
                break;
            }
            playNotes(action.notesToPlay);
            StatusWebsocket.sendToAll("next");
        }
        StatusWebsocket.sendToAll("stop");
    }

    private void initStrings() {
        List<PlectrumConfig> plectrumConfig = configRepository.loadPlectrumConfig();
        for (int i = 0; i < 6; i++) {
            PlectrumConfig config = plectrumConfig.get(i);
            controller.setServoPulse(config.adressHeight, config.portHeight, config.soft);
        }
        controller.waitMilliseconds(PREPARE_TIME);
    }

    public List<GuitarAction> getLastPlayedActions() {
        return lastPlayedActions;
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
