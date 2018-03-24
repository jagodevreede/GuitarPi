package nl.guitar.controlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StubController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(StubController.class);
    public void setServoPulse(int boardNumber, short port, float v) {
        logger.info("ServoPulse {}, {}, {}", boardNumber, port, v);
    }
}
