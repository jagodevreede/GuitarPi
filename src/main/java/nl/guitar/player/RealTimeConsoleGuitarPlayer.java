package nl.guitar.player;

import nl.guitar.player.object.GuitarNote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class RealTimeConsoleGuitarPlayer extends GuitarPlayer {

    private static final Logger logger = LoggerFactory.getLogger(RealTimeConsoleGuitarPlayer.class);

    private long playTime = 0;
    private Set<GuitarNote> notesPlayed = new HashSet<>();

    @Override
    void prepareString(GuitarNote gn) {
    }

    @Override
    void playString(GuitarNote gn) {
        if (gn.isHit()) {
            logger.info(gn.getName() + " on string " + gn.getStringNumber() + " on fred " + gn.getFred());
            notesPlayed.add(gn);
        }
    }

    @Override
    protected void waitMilliseconds(long waitTimeMS) {
        super.waitMilliseconds(waitTimeMS);
        playTime += waitTimeMS;
    }

    @Override
    public void close() throws Exception {
        logger.info("Play time was: " + playTime);
        logger.info("Played the following notes:");
        notesPlayed.stream().sorted((gn1, gn2) -> {
            if (gn1.getStringNumber() == gn2.getStringNumber()) {
                return Integer.compare(gn1.getFred(), gn2.getFred());
            }
            return Integer.compare(gn1.getStringNumber(), gn2.getStringNumber());
        }).forEach(gn -> {
            logger.info("String: " + gn.getStringNumber() + " fred: " + gn.getFred());
        });
    }
}
