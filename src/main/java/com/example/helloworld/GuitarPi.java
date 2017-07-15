package com.example.helloworld;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import nl.guitar.musicxml.MusicXmlParserListener;
import nl.guitar.player.ConsoleGuitarPlayer;
import nl.guitar.player.GuitarPlayer;
import nl.guitar.player.RealGuitarPlayer;
import org.jfugue.integration.MusicXmlParser;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class GuitarPi {

    private static class Arguments {
        @Parameter(names = "-console")
        private boolean console = false;

        @Parameter(names = "-file", required = true)
        private String file;
    }

    public static void main(String[] args) throws Exception {
        Arguments arguments = parseArguments(args);

        if (arguments.console) {
            try (GuitarPlayer gp = new ConsoleGuitarPlayer()) {
                run(gp, arguments.file);
            }
        } else {
            try (GuitarPlayer gp = new RealGuitarPlayer()) {
                run(gp, arguments.file);
            }
            System.out.println("Done playing");
            TimeUnit.SECONDS.sleep(2);
            System.out.println("exit!");
        }

    }

    public static void run(GuitarPlayer gp, String file) throws Exception {
        URL url = new File(file).toURI().toURL();
        String text = Resources.toString(url, Charsets.UTF_8);
        text = text.replaceAll("http://www.musicxml.org/dtds/partwise.dtd", "musicxml/partwise.dtd");

        MusicXmlParser parser = new MusicXmlParser();
        MusicXmlParserListener simpleParserListener = new MusicXmlParserListener(gp);
        parser.addParserListener(simpleParserListener);

        parser.parse(text);
        parser.fireAfterParsingFinished();
    }

    private static Arguments parseArguments(String[] args) {
        Arguments arguments = new Arguments();
        new JCommander(arguments, args);
        return arguments;
    }

}
