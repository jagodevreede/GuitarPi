package nl.guitar.player.object;

import java.util.ArrayList;
import java.util.List;

public class GuitarAction {

    public List<GuitarNote> notesToPlay = new ArrayList<>();
    public long timeTillNextNote;
    public long timeStamp;
    public int instructionNumber;
    public String error;

    public GuitarAction(){
        super();
    }

    @Override
    public String toString() {
        return "GuitarAction{" +
                "notesToPlay=" + notesToPlay +
                ", timeTillNextNote=" + timeTillNextNote +
                ", timeStamp=" + timeStamp +
                ", error=" + error +
                '}';
    }
}
