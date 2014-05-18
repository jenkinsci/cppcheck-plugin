/*******************************************************************************
 * Copyright (c) 2009-2011 Thales Corporate Services SAS                        *
 * Author : Gregory Boissinot                                                   *
 *                                                                              *
 * Permission is hereby granted, free of charge, to any person obtaining a copy *
 * of this software and associated documentation files (the "Software"), to deal*
 * in the Software without restriction, including without limitation the rights *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell    *
 * copies of the Software, and to permit persons to whom the Software is        *
 * furnished to do so, subject to the following conditions:                     *
 *                                                                              *
 * The above copyright notice and this permission notice shall be included in   *
 * all copies or substantial portions of the Software.                          *
 *                                                                              *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,     *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER       *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,*
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN    *
 * THE SOFTWARE.                                                                *
 *******************************************************************************/

package com.thalesgroup.hudson.plugins.cppcheck.model;

import hudson.model.ModelObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;

@ExportedBean(defaultVisibility = 999)
public class CppcheckFile implements ModelObject, Serializable {

    private static final long serialVersionUID = 3L;

    private Integer key;

    private String fileName;

    private int lineNumber;

    private String severity;

    private String cppCheckId;

    private String message;

    /** Verbose message, optional. */
    private String verbose = null;
    
    /** The issue may be false positive. */
    private boolean inconclusive = false;

    @Exported
    public String getFileName() {
        return fileName;
    }

    /**
     * Get the filename.
     * 
     * @return the filename or empty string if the filename is null
     */
    public String getFileNameNotNull() {
        return (fileName != null) ? fileName : "";
    }

    public void setFileName(String filename) {
        this.fileName = filename;
    }

    @Exported
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Get line number depending on availability of the file name.
     * 
     * @return the line number or empty string if the file name is empty
     */
    public String getLineNumberString() {
        return ("".equals(getFileNameNotNull())) ? "" : String.valueOf(lineNumber);
    }

    /**
     * Returns the line number that should be shown on top of the source code view.
     *
     * @return the line number
     */
    public int getLinkLineNumber() {
        return Math.max(1, lineNumber - 10);
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Exported
    public String getCppCheckId() {
        return cppCheckId;
    }

    public void setCppCheckId(String cppCheckId) {
        this.cppCheckId = cppCheckId;
    }

    @Exported
    public String getMessage() {
        return message;
    }
    
    public String getMessageHtml() {
        return StringEscapeUtils.escapeHtml(message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Exported
    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @Exported
    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    @Exported
    public String getVerbose() {
        return verbose;
    }
    
    public String getVerboseHtml() {
        return StringEscapeUtils.escapeHtml(verbose);
    }

    public void setVerbose(String verbose) {
        this.verbose = verbose;
    }

    @Exported
    public boolean isInconclusive() {
        return inconclusive;
    }

    public void setInconclusive(boolean inconclusive) {
        this.inconclusive = inconclusive;
    }

    public String getDisplayName() {
        return "cppcheckFile";
    }
}
