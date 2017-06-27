package com.example.helloworld;


import org.jfugue.theory.Note;

public class GuitarNote {
    private static final short E = 50;
    private short stringNumber = -1;
    private short fred;

    public GuitarNote(Note note) {
        System.out.println("note: " + note.getValue());
        if (note.getValue() >= 28) {
            stringNumber = 0;
        }
        if (note.getValue() >= 33) {
            stringNumber = 1;
        }
        if (note.getValue() >= 38) {
            stringNumber = 2;
        }
        if (note.getValue() >= 43) {
            stringNumber = 3;
        }
        if (note.getValue() >= 47) {
            stringNumber = 4;
        }
        if (note.getValue() >= 52) {
            stringNumber = 5;
        }
    }

    public short getStringNumber() {
        return stringNumber;
    }
}
