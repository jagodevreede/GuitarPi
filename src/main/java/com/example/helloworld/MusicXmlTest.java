package com.example.helloworld;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import i2c.servo.pwm.PCA9685;
import org.jfugue.integration.MusicXmlParser;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MusicXmlTest {

    private static final short[] servoLocations = new short[] { 4, 8, 5, 9, 6, 10};
    private static final float[] snarUp = new float[] { 1.4f, 1.3f, 1.3f, 1.4f, 1.3f, 1.36f};
    private static final float[] snarDown = new float[] { 1.6f, 1.15f, 1.7f, 1.25f, 1.55f, 1.1f};

    private static PCA9685 servoBoard;

    private static int min = 0;
    private static int max = 6;

    public static void main(String[] args) throws Exception {
        servoBoard = new PCA9685();   // 0x40 is the default address
        servoBoard.setPWMFreq(60); // Set frequency Hz
        reset();
        TimeUnit.MILLISECONDS.sleep(250);

        URL url = new File("Test.xml").toURI().toURL();
        String text = Resources.toString(url, Charsets.UTF_8);
        text = text.replaceAll("http://www.musicxml.org/dtds/partwise.dtd", "musicxml/partwise.dtd");

        MusicXmlParser parser = new MusicXmlParser();
        //SimpleParserListener simpleParserListener = new SimpleParserListener();
       // parser.addParserListener(simpleParserListener);

        parser.parse(text);

        /*

        int time = 500;

        for (int x = 0; x < 1; x++) {
            for (int i = min; i < max; i++) {
                servoBoard.setServoPulse(servoLocations[i], snarDown[i]);
                System.out.println("servo down " + i);
            TimeUnit.MILLISECONDS.sleep(time);
                System.out.println("servo ip " + i);
                servoBoard.setServoPulse(servoLocations[i], snarUp[i]);
            }
            TimeUnit.MILLISECONDS.sleep(time);
        }

        TimeUnit.MILLISECONDS.sleep(1000);

        reset(); */
    }

    private static void reset() throws InterruptedException {
        System.out.println("Reset servo positions");
        for (int i = min; i < max; i++) {
            servoBoard.setServoPulse(servoLocations[i],snarUp[i]);
          //  TimeUnit.MILLISECONDS.sleep(1000);
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }
}
