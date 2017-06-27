package com.example.helloworld;

import com.pi4j.io.i2c.I2CFactory;
import i2c.servo.pwm.PCA9685;
import org.jfugue.theory.Note;

import java.util.concurrent.TimeUnit;

public class RealGuitarPlayer extends GuitarPlayer {

    private static final short[] servoLocations = new short[] { 4, 8, 5, 9, 6, 10};
    private static final float[] stringUp = new float[] { 1.4f, 1.3f, 1.3f, 1.4f, 1.3f, 1.36f};
    private static final float[] stringDown = new float[] { 1.6f, 1.15f, 1.6f, 1.25f, 1.55f, 1.1f};
    private boolean[] isStringUp = new boolean[] { true, true, true, true, true, true };

    private static PCA9685 servoBoard;

    public RealGuitarPlayer() {
        try {
            servoBoard = new PCA9685();   // 0x40 is the default address
        } catch (I2CFactory.UnsupportedBusNumberException e) {
            throw new RuntimeException(e);
        }
        servoBoard.setPWMFreq(60); // Set frequency Hz
        close();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Real guitar player ready!");
    }

    void playString(short stringNumber) {
        if (stringNumber < 0) {
            return;
        }
        float toPos;
        if (isStringUp[stringNumber]) {
            toPos = stringDown[stringNumber];
            isStringUp[stringNumber] = false;
        } else {
            toPos = stringUp[stringNumber];
            isStringUp[stringNumber] = true;
        }
        servoBoard.setServoPulse(servoLocations[stringNumber], toPos);
    }

    @Override
    public void close() {
        System.out.println("Reset servo positions");
        for (int i = 0; i < 6; i++) {
            servoBoard.setServoPulse(servoLocations[i], stringUp[i]);
        }
    }
}
