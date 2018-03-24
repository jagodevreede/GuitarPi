package nl.guitar;

import io.dropwizard.Configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GuitarAppConfiguration extends Configuration   {
    private String implementation = "Console";
    private String controller = "Stub";

    @JsonProperty
    public String getImplementation() {
        return implementation;
    }

    @JsonProperty
    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    @JsonProperty
    public String getController() {
        return controller;
    }

    @JsonProperty
    public void setController(String controller) {
        this.controller = controller;
    }

}
