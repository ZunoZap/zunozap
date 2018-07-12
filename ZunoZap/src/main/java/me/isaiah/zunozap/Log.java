package me.isaiah.zunozap;

import java.io.OutputStream;
import java.io.PrintStream;

public class Log extends PrintStream {
    public Log(OutputStream out) { super(out); }

    @Override public void println(String o) { info(o); }
    public void info(String o) { super.println("[INFO] " + o); }
    public void err(String o) { super.println("[ERR] " + o); }
}
