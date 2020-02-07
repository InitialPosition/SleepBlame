package de.SYRAPT0R.sleepblame;

import java.text.MessageFormat;

public class Logging {
    protected static void consoleLog(String message) {
        System.out.println(MessageFormat.format("[SleepBlame] {0}", message));
    }
}
