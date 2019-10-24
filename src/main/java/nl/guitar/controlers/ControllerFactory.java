package nl.guitar.controlers;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.inject.Produces;

public class ControllerFactory {

    @ConfigProperty(name = "controller")
    String controller;

    @Produces
    public Controller createController() {
        switch (controller) {
            case "ConsoleController":
                return new ConsoleController();
            case "NoOpController":
                return new NoOpController();
            case "RealController":
                return new RealController();
            default:
                throw new IllegalStateException("Unexpected controller value: " + controller);
        }
    }
}
