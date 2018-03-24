package nl.guitar.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestState {
    public Position position;
    public Height height;

    @JsonCreator
    private TestState(@JsonProperty("position") String position, @JsonProperty("height") String height) {
        if (position != null) {
            this.position = Position.valueOf(position.toUpperCase());
        }
        if (height != null) {
            this.height = Height.valueOf(height.toUpperCase());
        }
    }

    @Override
    public String toString() {
        return "TestState{" +
                "position=" + position +
                ", height=" + height +
                '}';
    }
}
