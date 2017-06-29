package com.example.helloworld;

import com.pi4j.io.i2c.I2CFactory;
import i2c.servo.pwm.PCA9685;

import java.util.concurrent.TimeUnit;

public class TestController {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Start with: address location pulse");
            return;
        }
        int address = Integer.parseInt(args[0]);
        PCA9685 servoBoard;
        try {
            servoBoard = new PCA9685(address);   // 0x40 is the default address
        } catch (I2CFactory.UnsupportedBusNumberException e) {
            throw new RuntimeException(e);
        }
        servoBoard.setPWMFreq(60); // Set frequency Hz

        System.out.println("Using i2c address " + Integer.toHexString(address) + ":" + Integer.parseInt(args[1]) + " pulse: " + Float.parseFloat(args[2]));
        servoBoard.setServoPulse(Integer.parseInt(args[1]), Float.parseFloat(args[2]));
        TimeUnit.SECONDS.sleep(1);
    }
}
