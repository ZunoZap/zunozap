package me.isaiah.zunozap;

import java.io.OutputStream;
import java.io.PrintStream;

public class Log extends PrintStream {
    public Log(OutputStream out) { super(out); }

    @Override public void println(String o) {
        super.println("[INFO] " + o);
    }
}
