package org.jenkinsci.plugins.cppcheck;

import hudson.model.AbstractBuild;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckWorkspaceFile;

/**
 * Show all violations highlighted on a single page.
 * 
 * @author Michal Turek
 * @since 1.16
 */
public class CppcheckSourceAll {
    /** The related build. */
    private final AbstractBuild<?, ?> owner;

    /** The files to show. */
    private final Collection<CppcheckWorkspaceFile> files;

    /** Number of lines to show before the highlighted line. */
    private final int linesBefore;

    /** Number of lines to show after the highlighted line. */
    private final int linesAfter;

    /**
     * Constructor.
     * 
     * @param owner
     *            the related build
     * @param files
     *            the files to show
     * @param linesBefore
     *            number of lines to show before the highlighted line
     * @param linesAfter
     *            number of lines to show after the highlighted line
     */
    public CppcheckSourceAll(AbstractBuild<?, ?> owner,
            Collection<CppcheckWorkspaceFile> files, int linesBefore,
            int linesAfter) {
        this.owner = owner;
        this.files = files;
        this.linesBefore = linesBefore;
        this.linesAfter = linesAfter;
    }

    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }

    public Collection<CppcheckWorkspaceFile> getFiles() {
        return files;
    }

    public int getLinesBefore() {
        return linesBefore;
    }

    public int getLinesAfter() {
        return linesAfter;
    }

    /**
     * Get specified lines of source code from the file.
     * 
     * @param file
     *            the input file
     * @return the related lines of code with HTML formatting
     */
    public String getSourceCode(CppcheckWorkspaceFile file) {
        File tempFile = new File(file.getTempName(owner));

        if (!tempFile.exists()) {
            return "Can't read file: " + tempFile.getAbsolutePath();
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(tempFile));
            return getRelatedLines(reader, file.getCppcheckFile()
                    .getLineNumber());
        } catch (FileNotFoundException e) {
            return "Can't read file: " + e;
        } catch (IOException e) {
            return "Reading file failed: " + e;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Get specified lines from a stream.
     * 
     * @param reader
     *            the input stream
     * @param lineNumber
     *            the base line
     * @return the lines with HTML formatting
     * @throws IOException
     *             if something fails
     */
    private String getRelatedLines(BufferedReader reader, int lineNumber)
            throws IOException {
        final int start = (lineNumber > linesBefore) ? lineNumber - linesBefore : 1;
        final int end = lineNumber + linesAfter;
        final String numberFormat = "%0" + String.valueOf(end).length() + "d";

        StringBuilder builder = new StringBuilder();
        int current = 1;
        String line = "";

        while ((line = reader.readLine()) != null && current <= end) {
            if (current >= start) {
                if (current == lineNumber) {
                    builder.append("<div class=\"line highlighted\">");
                } else {
                    builder.append("<div class=\"line\">");
                }

                builder.append("<span class=\"lineNumber\">");
                builder.append(String.format(numberFormat, current));
                builder.append("</span> ");// The space separates line number and code
                builder.append(StringEscapeUtils.escapeHtml(line));
                builder.append("</div>\n");
            }

            ++current;
        }

        return builder.toString();
    }
}
