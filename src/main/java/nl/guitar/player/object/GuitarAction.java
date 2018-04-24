package nl.guitar.player.object;

import java.util.List;

public class GuitarAction {

    public List<GuitarNote> notesToPlay;
    public long timeTillNextNote;
    public long timeStamp;

    @Override
    public String toString() {
        return "GuitarAction{" +
                "notesToPlay=" + notesToPlay +
                ", timeTillNextNote=" + timeTillNextNote +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
