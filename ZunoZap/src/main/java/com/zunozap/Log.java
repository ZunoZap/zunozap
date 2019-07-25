package com.zunozap;

import java.io.OutputStream;
import java.io.PrintStream;

public class Log extends PrintStream {

    public static final Log out = new Log(System.out);
    public static final Log err = new Log(System.err);

    public static final void out(Object o) { out.log(o); }
    public static final void err(Object o) { err.log(o); } 

    public Log(OutputStream out) { super(out); }

    public void log(Object o) { super.println((System.out.equals(this) ? "INFO: " : "ERROR: ") + o); }

}