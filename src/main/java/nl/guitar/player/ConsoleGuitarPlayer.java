package nl.guitar.player;

import nl.guitar.controlers.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleGuitarPlayer extends RealTimeConsoleGuitarPlayer {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleGuitarPlayer.class);

    public ConsoleGuitarPlayer(Controller controller) {
        super(controller);
    }

    @Override
    protected void waitMilliseconds(long waitTimeMS) {
        logger.info("Next note in: " + waitTimeMS + "ms");
    }

}
