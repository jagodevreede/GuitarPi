package com.example.helloworld;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jfugue.integration.MusicXmlParser;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting application");
        new HelloWorldApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        // Enable variable substitution with environment variables

        MusicXmlParser mxp = new MusicXmlParser();
    }

    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) {
        System.out.println("Running");
    }
}
