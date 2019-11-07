package nl.guitar.player;

import io.quarkus.runtime.StartupEvent;
import nl.guitar.StatusWebsocket;
import nl.guitar.controlers.Controller;
import nl.guitar.data.ConfigRepository;
import nl.guitar.domain.FredConfig;
import nl.guitar.domain.PlectrumConfig;
import nl.guitar.player.object.GuitarAction;
import nl.guitar.player.object.GuitarNote;
import nl.guitar.player.object.NoteComparator;
import nl.guitar.player.strategy.StringStrategy;
import nl.guitar.player.tuning.GuitarTuning;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jfugue.theory.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class GuitarPlayer implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(GuitarPlayer.class);
    public static final int PREPARE_TIME = 150;
    protected final Controller controller;
    private final ConfigRepository configRepository;
    private int notesBarsPlayed;
    private float tempo = 60;
    private List<GuitarAction> lastPlayedActions;

    private List<PlectrumConfig> plectrumConfig;
    private List<List<FredConfig>> fredConfig;

    private boolean[] isStringUp = new boolean[] { true, true, true, true, true, true };
    private int[] fredPressed = new int[] { 0, 0, 0, 0, 0, 0 };
    private long[] fredCount = new long[] { 0, 0, 0, 0, 0, 0 };

    @ConfigProperty(name = "reset.on.startup", defaultValue = "true")
    String RESET_ON_STARTUP = "true";

    void onStart(@Observes StartupEvent ev) {
        logger.info("GuitarPi is starting...");
    }

    public GuitarPlayer(Controller controller, ConfigRepository configRepository) {
        this.controller = controller;
        this.configRepository = configRepository;
    }

    @PostConstruct
    public void postConstruct() {
        if (Boolean.parseBoolean(RESET_ON_STARTUP)) {
            try {
                close();
            } catch (InterruptedException e) {
                logger.error("Failed to load guitar player", e);
                throw new RuntimeException(e);
            }
        } else {
            logger.info("Not resetting frets as config set to skip");
        }
        logger.info("Guitar player ready!");
        this.resetFreds();
    }

    public GuitarAction calculateNotes(List<Note> notes, GuitarTuning guitarTuning, GuitarAction lastAction, StringStrategy stringStrategy) {
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
                if (!note.isRest()) {
                    GuitarNote gn = new GuitarNote(note, guitarTuning, stringsTaken, note.getDuration(), stringStrategy);
                    notesToPlay.add(gn);
                    if (gn.getStringNumber() == -1) {
                        action.error = "[" + stringStrategy.getClass().getSimpleName() +"] Unable to play @" + action.instructionNumber + " note value " + note.getValue() + ' ' + note;
                        logger.warn(action.error);
                    } else {
                        stringsTaken[gn.getStringNumber()] = gn.getNoteValue();
                    }
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

            List<Short> distinctStrings = notesToPlay.stream().map(GuitarNote::getStringNumber).filter(s -> s >= 0).distinct().collect(Collectors.toList());
            if (notesToPlay.size() > distinctStrings.size()) {
                logger.debug("Want to play a sting multiple times on note: " + notesBarsPlayed + " on string " + distinctStrings + " time since last note:" + (lastAction != null ? lastAction.timeTillNextNote : -10));
            }

            action.notesToPlay = notesToPlay;
        } catch (Exception e) {
            logger.error("Note calculation error for note ("+ action.instructionNumber +"): " + e.getMessage());
            action.error = e.getMessage();
        }
        return action;
    }

    public void printStats(List<GuitarAction> actions) {
        logger.info("Number statistics:");
        if (actions.isEmpty()) {
            logger.info("  No actions!");
            return;
        }
        LongSummaryStatistics durationStats = actions.stream()
                .map((GuitarAction a) -> a.timeTillNextNote)
                .collect(LongSummaryStatistics::new,
                        LongSummaryStatistics::accept,
                        LongSummaryStatistics::combine);
        final List<Integer> notes = actions.stream()
                .flatMap((GuitarAction a) -> a.notesToPlay.stream()
                        .map(GuitarNote::getNoteValue)
                ).filter(i -> i > 0)
                .collect(Collectors.toList());
        String lowestNote = notes.stream()
                .min(Integer::compare)
                .toString();
        String highestNote = notes.stream()
                .max(Integer::compare)
                .toString();
        logger.info("  Errors: {}", actions.stream().filter(a -> a.error != null).count());
        actions.stream().filter(a -> a.error != null).forEach(a -> logger.info("   - Error @{}: {}", a.instructionNumber, a.error));
        logger.info("  Shortest note {}ms", durationStats.getMin());
        logger.info("  Average note {}ms", durationStats.getAverage());
        logger.info("  Lowest note {}", lowestNote);
        logger.info("  Highest note {}", highestNote);
    }

    private void playNotes(List<GuitarNote> notesToPlay, long timeStampWhenNoteShouldSound){
        notesBarsPlayed++;
        notesToPlay.forEach(this::prepareStringPressFredAndMovePlectrumToHigh);
        controller.waitMilliseconds(PREPARE_TIME /3);
        notesToPlay.forEach(this::prepareStringMovePlectrumToUp);
        controller.waitMilliseconds(PREPARE_TIME /3);
        notesToPlay.forEach(this::prepareStringMovePlectrumToHitPosition);

        controller.waitUntilTimestamp(timeStampWhenNoteShouldSound);

        logger.info("Playing notes [{}]: @{}: {}", notesToPlay.size(), timeStampWhenNoteShouldSound, notesToPlay);
        notesToPlay.forEach(this::playString);
    }

    private ThreadPoolExecutor executorService = null;

    public void playActions(List<GuitarAction> guitarActions) {
        executorService = new ThreadPoolExecutor(4, 4,60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        reloadConfig();
        this.lastPlayedActions = new ArrayList<>(guitarActions);
        initStrings();
        StatusWebsocket.sendToAll("start");
        controller.start(PREPARE_TIME + 500);
        for (GuitarAction action : guitarActions) {
            executorService.execute(() -> {
                    controller.waitUntilTimestamp(action.timeStamp - PREPARE_TIME);
                playNotes(action.notesToPlay, action.timeStamp);
                StatusWebsocket.sendToAll("next");
            });
        }
        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("Failed waiting for termination of thread pool", e);
        }
        StatusWebsocket.sendToAll("stop");
    }

    private void initStrings() {
        reloadConfig();
        for (int i = 0; i < 6; i++) {
            PlectrumConfig config = plectrumConfig.get(i);
            controller.setServoPulse(config.adressHeight, config.portHeight, config.soft);
        }
        controller.waitMilliseconds(PREPARE_TIME);
    }

    public List<GuitarAction> getLastPlayedActions() {
        return lastPlayedActions;
    }

    public void setTempo(int tempo) {
        logger.info("Tempo changed to = " + tempo);
        this.tempo = tempo;
    }

    public void stop() {
        executorService.shutdownNow();
    }

    void prepareStringPressFredAndMovePlectrumToHigh(GuitarNote gn) {
        if (gn.getStringNumber() == -1 && gn.getNoteValue() > 0) {
            logger.debug("Not a correct string for {}", gn);
            return;
        }
        int stringNumber = gn.getStringNumber();
        int fredNumber = gn.getFred();

        if (fredPressed[stringNumber] != fredNumber) {
            if (fredPressed[stringNumber] > 0) {
                resetFred(stringNumber);
            }
            fredPressed[stringNumber] = fredNumber;
            if (fredNumber > 0) {
                logger.trace("Press fred {}", fredNumber);
                FredConfig fc = fredConfig.get(stringNumber).get(fredNumber - 1);
                controller.setServoPulse(fc.address, fc.port, fc.push);
            }
        }
        PlectrumConfig stringConfig = plectrumConfig.get(stringNumber);
        controller.setServoPulse(stringConfig.adressHeight, stringConfig.portHeight, stringConfig.free);
    }

    void prepareStringMovePlectrumToUp(GuitarNote gn) {
        final int stringNumber = gn.getStringNumber();
        if (gn.getStringNumber() == -1 || !gn.isHit()) {
            return;
        }

        PlectrumConfig stringConfig = plectrumConfig.get(stringNumber);

        controller.setServoPulse(stringConfig.adressPlectrum, stringConfig.portPlectrum, stringConfig.up);
        isStringUp[stringNumber] = true;
    }

    void prepareStringMovePlectrumToHitPosition(GuitarNote gn) {
        final int stringNumber = gn.getStringNumber();
        if (stringNumber == -1 || !gn.isHit()) {
            return;
        }
        PlectrumConfig stringConfig = plectrumConfig.get(stringNumber);

        float heightDistance = stringConfig.hard - stringConfig.soft;
        float height = stringConfig.soft;
        if (fredPressed[stringNumber] > 1) {
            height = stringConfig.soft + (heightDistance / fredCount[stringNumber] * (fredPressed[stringNumber] -1));
        }
        controller.setServoPulse(stringConfig.adressHeight, stringConfig.portHeight, height);
    }

    void playString(GuitarNote gn) {
        if (gn.getStringNumber() == -1 || !gn.isHit()) {
            return;
        }
        float toPos;
        PlectrumConfig stringConfig = plectrumConfig.get(gn.getStringNumber());
        if (isStringUp[gn.getStringNumber()]) {
            toPos = stringConfig.down;
            isStringUp[gn.getStringNumber()] = false;
        } else {
            toPos = stringConfig.up;
            isStringUp[gn.getStringNumber()] = true;
        }
        controller.setServoPulse(stringConfig.adressPlectrum, stringConfig.portPlectrum, toPos);
    }

    public void resetFreds() {
        logger.debug("Resetting freds");
        for (int i = 0; i < 6; i++) {
            resetFred(i);
            controller.waitMilliseconds(350);
        }
        controller.waitMilliseconds(100);
        logger.debug("Resetting freds ready");
    }

    private void resetFred(int stringNumber) {
        for (int i = 0; i < fredConfig.get(stringNumber).size(); i += 2) {
            FredConfig fc = fredConfig.get(stringNumber).get(i);
            if (fc.port > -1) {
                controller.setServoPulse(fc.address, fc.port, fc.free);
                logger.trace("reset fred servo {}", fc);
            }
        }
    }

    private void reloadConfig() {
        plectrumConfig = configRepository.loadPlectrumConfig();
        fredConfig = configRepository.loadFredConfig();
    }

    @Override
    public void close() throws InterruptedException {
        logger.debug("Reset servo positions");
        reloadConfig();
        for (int i = 0; i < 6; i++) {
            fredCount[i] = fredConfig.get(i).stream().filter(f -> f.port > -1).count();
            List<FredConfig> configs = fredConfig.get(i);
            for (int j = 0; j < configs.size(); j += 2) {
                FredConfig fredConfig = configs.get(j);
                if (fredConfig.port > -1) {
                    controller.setServoPulse(fredConfig.address, fredConfig.port, fredConfig.push);
                    Thread.sleep(GuitarPlayer.PREPARE_TIME);
                    controller.setServoPulse(fredConfig.address, fredConfig.port, fredConfig.free);
                }
            }
            PlectrumConfig config = plectrumConfig.get(i);
            controller.setServoPulse(config.adressHeight, config.portHeight, config.free);
            Thread.sleep(PREPARE_TIME);
            controller.setServoPulse(config.adressPlectrum, config.portPlectrum, config.up);
        }
    }

}
