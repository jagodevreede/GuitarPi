package nl.guitar.player;

import nl.guitar.player.object.GuitarNote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ConsoleGuitarPlayer extends RealTimeConsoleGuitarPlayer {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleGuitarPlayer.class);

    @Override
    protected void waitMilliseconds(long waitTimeMS) {
        logger.info("Next note in: " + waitTimeMS + "ms");
    }

}
