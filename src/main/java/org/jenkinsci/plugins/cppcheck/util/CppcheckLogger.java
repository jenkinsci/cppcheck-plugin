package org.jenkinsci.plugins.cppcheck.util;

import hudson.model.BuildListener;
import hudson.model.TaskListener;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class CppcheckLogger implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Log output to the given logger, using the Cppcheck identifier
     *
     * @param listener The current listener
     * @param message  The message to be outputted
     */
    public static void log(BuildListener listener, final String message) {
        listener.getLogger().println("[Cppcheck] " + message);
    }

    public static void log(TaskListener listener, final String message) {
        listener.getLogger().println("[Cppcheck] " + message);
    }
}