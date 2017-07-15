package nl.guitar.player;

import nl.guitar.player.object.GuitarNote;
import com.pi4j.io.i2c.I2CFactory;
import i2c.servo.pwm.PCA9685;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RealGuitarPlayer extends GuitarPlayer {

    private static final Logger logger = LoggerFactory.getLogger(RealGuitarPlayer.class);

    private static final short[] servoLocations = new short[] { 4, 8, 5, 9, 6, 10};
    private static final float[] stringUp = new float[] { 1.4f, 1.3f, 1.3f, 1.4f, 1.3f, 1.36f};
    private static final float[] stringDown = new float[] { 1.6f, 1.1f, 1.5f, 1.25f, 1.55f, 1.1f};
    private boolean[] isStringUp = new boolean[] { true, true, true, true, true, true };
    private int[] fredPressed = new int[] { 0, 0, 0, 0, 0, 0 };

    private static final short[][] servoFredLocations = new short[][] {
            {4,4,3,3,-1,-1,-1,-1,-1,-1}, // E BAS
            {5,5,2,2,-1,-1,-1,-1,-1,-1,-1,-1}, // B
            {6,6,1,1,-1,-1,-1,-1,-1,-1,-1,-1}, // G
            {13,13,9,9,-1,-1,-1,-1,-1,-1,-1,-1}, // D
            {14,14,10,10,-1,-1,-1,-1,-1,-1,-1,-1}, // B
            {15,15,11,11,8,8,7,7,-1,-1,-1,-1}  // E
    };

    private static final float[][] servoFredPositions= new float[][] {
            {1.10f,1.80f,1.10f,1.90f,0,0,0,0,0,0,0,0}, // E BAS
            {1.20f,1.85f,1.10f,2.00f,0,0,0,0,0,0,0,0}, // B
            {1.20f,1.85f,1.20f,1.80f,0,0,0,0,0,0,0,0}, // G
            {1.70f,1.00f,1.80f,1.20f,0,0,0,0,0,0,0,0}, // D
            {1.80f,1.20f,1.70f,1.20f,0,0,0,0,0,0,0,0}, // B
            {1.85f,1.15f,1.9f,1.25f,1.90f,1.20f,1.65f,1.2f,0,0,0,0}  // E
    };

    private static final float[][] servoFredCenterPositions= new float[][] {
            {1.45f,1.45f,0,0,0,0,0}, // E BAS
            {1.51f,1.51f,0,0,0,0,0}, // B
            {1.51f,1.51f,0,0,0,0,0}, // G
            {1.45f,1.51f,0,0,0,0,0}, // D
            {1.45f,1.40f,0,0,0,0,0}, // B
            {1.51f,1.51f,1.50f,1.45f,0,0,0}  // E
    };

    private static PCA9685 servoBoardStrings;
    private static PCA9685 servoBoardFreds;

    static {
        try {
            servoBoardStrings = new PCA9685(0x40);
            servoBoardStrings.setPWMFreq(120); // Set frequency Hz
            TimeUnit.SECONDS.sleep(1);
            servoBoardFreds = new PCA9685(0x41);
            servoBoardFreds.setPWMFreq(120); // Set frequency Hz
            TimeUnit.SECONDS.sleep(1);
        } catch (I2CFactory.UnsupportedBusNumberException | InterruptedException | UnsatisfiedLinkError e) {
            logger.error("Failed to load real guitar player", e);
            throw new RuntimeException(e);
        }
    }

    public RealGuitarPlayer() {
        logger.info("Starting real guitar player");
        try {
            close();
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException  e) {
            logger.error("Failed to load real guitar player", e);
            throw new RuntimeException(e);
        }
        logger.info("Real guitar player ready!");
    }

    @Override
    void prepareString(GuitarNote gn) {
        int stringNumber = gn.getStringNumber();
        int fredNumber = gn.getFred();

        if (fredPressed[stringNumber] != fredNumber) {
            if (fredPressed[stringNumber] > 0) {
                resetFred(stringNumber);
            }
            fredPressed[stringNumber] = fredNumber;
            if (fredNumber > 0) {
                logger.info("Press fred " + fredNumber);
                servoBoardFreds.setServoPulse(servoFredLocations[stringNumber][fredNumber - 1], servoFredPositions[stringNumber][fredNumber - 1]);
            }
        }
    }

    void playString(GuitarNote gn) {
        if (gn.isHit()) {
            float toPos;
            if (isStringUp[gn.getStringNumber()]) {
                toPos = stringDown[gn.getStringNumber()];
                isStringUp[gn.getStringNumber()] = false;
            } else {
                toPos = stringUp[gn.getStringNumber()];
                isStringUp[gn.getStringNumber()] = true;
            }
            servoBoardStrings.setServoPulse(servoLocations[gn.getStringNumber()], toPos);
        }
    }

    @Override
    protected void resetFreds() {
        super.resetFreds();
        for (int i = 0; i < 6; i++) {
            resetFred(i);
        }
    }

    private void resetFred(int stringNumber) {
        for (int i = 0; i < servoFredLocations[0].length; i++) {
            if (servoFredLocations[stringNumber][i] > -1) {
                servoBoardFreds.setServoPulse(servoFredLocations[stringNumber][i], servoFredCenterPositions[stringNumber][i/2]);
                logger.debug("fred servo "+ servoFredLocations[stringNumber][i] + " pos "  + servoFredCenterPositions[stringNumber][i/2]);
            }
        }
    }

    @Override
    public void close() {
        logger.info("Reset servo positions");
        for (int i = 0; i < 6; i++) {
            resetFred(i);
            servoBoardStrings.setServoPulse(servoLocations[i], stringUp[i]);
        }
    }
}
