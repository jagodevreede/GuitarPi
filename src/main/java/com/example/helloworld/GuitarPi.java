package com.example.helloworld;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import i2c.servo.pwm.PCA9685;
import org.jfugue.integration.MusicXmlParser;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class GuitarPi {

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "console".equals(args[0])) {
            try (GuitarPlayer gp = new ConsoleGuitarPlayer()) {
                run(gp);
            }
        } else {
            try (GuitarPlayer gp = new RealGuitarPlayer()) {
                run(gp);
            }
        }

    }

    public static void run(GuitarPlayer gp) throws Exception {
        URL url = new File("Test.xml").toURI().toURL();
        String text = Resources.toString(url, Charsets.UTF_8);
        text = text.replaceAll("http://www.musicxml.org/dtds/partwise.dtd", "musicxml/partwise.dtd");

        MusicXmlParser parser = new MusicXmlParser();
        SimpleParserListener simpleParserListener = new SimpleParserListener(gp);
        parser.addParserListener(simpleParserListener);

        parser.parse(text);
    }
}
