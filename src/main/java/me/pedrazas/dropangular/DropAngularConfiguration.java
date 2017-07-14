package me.pedrazas.dropangular;

import io.dropwizard.Configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DropAngularConfiguration extends Configuration   {
    private String defaultName = "DropAngular Service";
    
    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }

}
