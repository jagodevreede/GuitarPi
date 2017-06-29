package com.example.helloworld;


import org.jfugue.theory.Note;

public class GuitarNote {
    private static final int[] stringStartNote = new int[] { 28, 33, 38, 43, 47, 52};
    private int stringNumber = -1;
    private int fred;

    public GuitarNote(Note note) {
        System.out.println("note: " + note.getValue() + " = " + note.toString());
        for (short i = 0; i < 6; i++) {
            if (note.getValue() >= stringStartNote[i]) {
                stringNumber = i;
            }
        }
        if (stringNumber >= 0) {
            fred = note.getValue() - stringStartNote[stringNumber];
        }
    }

    public int getStringNumber() {
        return stringNumber;
    }

    public int getFred() {
        return fred;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GuitarNote that = (GuitarNote) o;

        if (stringNumber != that.stringNumber) return false;
        return fred == that.fred;
    }

    @Override
    public int hashCode() {
        int result = stringNumber;
        result = 31 * result + fred;
        return result;
    }
}
