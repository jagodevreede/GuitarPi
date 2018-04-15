package nl.guitar;

import io.dropwizard.Configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GuitarAppConfiguration extends Configuration {
    private String controller = "Stub";

    @JsonProperty
    public String getController() {
        return controller;
    }

    @JsonProperty
    public void setController(String controller) {
        this.controller = controller;
    }

}
