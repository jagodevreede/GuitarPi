package nl.guitar.controlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleController extends RealTimeController  {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleController.class);
    public void setServoPulse(int boardNumber, short port, float v) {
        logger.debug("ServoPulse {}, {}, {}", boardNumber, port, v);
    }
}
