package nl.guitar.player.object;

import org.jfugue.theory.Note;

import java.util.Comparator;

public class NoteComparator implements Comparator<Note> {
    public static final NoteComparator INSTANCE = new NoteComparator();

    @Override
    public int compare(Note o1, Note o2) {
        return o1.getValue() < o2.getValue() ? -1 : o1.getValue() == o2.getValue() ? 0 : 1;
    }
}
