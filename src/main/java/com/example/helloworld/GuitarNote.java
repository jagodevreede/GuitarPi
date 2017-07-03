package com.example.helloworld;


import org.jfugue.theory.Note;

public class GuitarNote {
    private static final int[] stringStartNote = new int[] { 28, 33, 38, 43, 47, 52};
    private int stringNumber = -1;
    private int fred = 0;
    private boolean hit = true;
    private String name;

    public GuitarNote(Note note) {
        name = note.toString();
        int noteValue = note.getValue();
       /* if (noteValue > 56) {
            noteValue -= 12;
            System.out.println("Note " + note + " is to high (" + note.getValue() + ") using a octave lower");
        }*/
        for (short i = 0; i < 6; i++) {
            if (noteValue >= stringStartNote[i]) {
                stringNumber = i;
            }
        }
        if (stringNumber >= 0) {
            fred = noteValue - stringStartNote[stringNumber];
        }
    }

    public GuitarNote(int stringNumber, int fred, boolean hit) {
        this.stringNumber = stringNumber;
        this.fred = fred;
        this.hit = hit;
    }

    public int getStringNumber() {
        return stringNumber;
    }

    public int getFred() {
        return fred;
    }

    public boolean isHit() {
        return hit;
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

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "GuitarNote{" +
                "stringNumber=" + stringNumber +
                ", fred=" + fred +
                ", hit=" + hit +
                ", name='" + name + '\'' +
                '}';
    }
}
