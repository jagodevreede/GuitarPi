package nl.guitar.controlers;

import com.pi4j.io.i2c.I2CFactory;
import i2c.servo.pwm.PCA9685;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RealController extends RealTimeController  {
    private static final Logger logger = LoggerFactory.getLogger(RealController.class);
    private static final int updateFrequencyHz = 75; // max 200

    private static PCA9685[] servoBoards;

    static {
        try {
            logger.info("Starting REAL controller (works only on the PI)");
            servoBoards = new PCA9685[]{ new PCA9685(0x40), new PCA9685(0x41) };
            servoBoards[0].setPWMFreq(updateFrequencyHz);
            TimeUnit.SECONDS.sleep(1);
            servoBoards[1].setPWMFreq(updateFrequencyHz);
            TimeUnit.SECONDS.sleep(1);
        } catch (I2CFactory.UnsupportedBusNumberException | InterruptedException | UnsatisfiedLinkError e) {
            logger.error("Failed to load real guitar player", e);
            throw new RuntimeException(e);
        }
    }

    public void setServoPulse(int boardNumber, short port, float v) {
        servoBoards[boardNumber].setServoPulse(port, v);
    }
}
