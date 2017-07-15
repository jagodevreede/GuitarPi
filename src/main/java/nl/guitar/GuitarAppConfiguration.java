package nl.guitar;

import io.dropwizard.Configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GuitarAppConfiguration extends Configuration   {
    private String implementation = "Console";

    @JsonProperty
    public String getImplementation() {
        return implementation;
    }

    @JsonProperty
    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

}
