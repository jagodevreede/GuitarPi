package com.example.helloworld;

import i2c.servo.pwm.PCA9685;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jfugue.integration.MusicXmlParser;

import java.util.concurrent.TimeUnit;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting application");
        // new HelloWorldApplication().run(args);

        PCA9685 servoBoard = new PCA9685();   // 0x40 is the default address
        servoBoard.setPWMFreq(50); // Set frequency Hz
        //int servoMin = 150;   // Min pulse length out of 4096
        //int servoMax = 600;   // Max pulse length out of 4096

        final int STANDARD_SERVO_CHANNEL = 1;

        for (int i=0; i<10; i++)
        {
            System.out.println("i=" + i);
            servoBoard.setServoPulse(STANDARD_SERVO_CHANNEL,1.4f);
            TimeUnit.SECONDS.sleep(1);
            //servoBoard.setServoPulse(STANDARD_SERVO_CHANNEL,1.51f);
            TimeUnit.SECONDS.sleep(1);
            servoBoard.setServoPulse(STANDARD_SERVO_CHANNEL,1.6f);
            TimeUnit.SECONDS.sleep(1);
        }
        servoBoard.setPWM(STANDARD_SERVO_CHANNEL,0, 0);
        System.out.println("Done with the demo.");
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        // Enable variable substitution with environment variables

        //MusicXmlParser mxp = new MusicXmlParser();
    }

    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) {
        System.out.println("Running");
    }
}
