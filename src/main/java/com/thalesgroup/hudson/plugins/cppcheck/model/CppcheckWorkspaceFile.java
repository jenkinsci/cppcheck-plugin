/*******************************************************************************
 * Copyright (c) 2009 Thales Corporate Services SAS                             *
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

import hudson.model.AbstractBuild;

import java.io.File;

import org.apache.commons.lang.StringUtils;

public class CppcheckWorkspaceFile {

    /**
     * Temporary directory holding the workspace files.
     */
    public static final String WORKSPACE_FILES = "workspace-files";

    private String fileName;

    private CppcheckFile cppcheckFile;

    /**
     * Useful for files that are not found on the buid filestystem
     */
    private boolean sourceIgnored;

    public CppcheckWorkspaceFile(File file) {
        if (file != null)
            this.fileName = file.getAbsolutePath().replace('\\', '/');
    }

    public CppcheckWorkspaceFile() {

    }


    public CppcheckFile getCppcheckFile() {
        return cppcheckFile;
    }


    public void setCppcheckFile(CppcheckFile cppcheckFile) {
        this.cppcheckFile = cppcheckFile;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (fileName == null){
            setSourceIgnored(true);
        }
    }


    /**
     * Returns a file name for a temporary file that will hold the contents of the source.
     *
     * @return the temporary name
     */
    public String getTempName() {
        return Integer.toHexString(getFileName().hashCode()) + ".tmp";
    }

    /**
     * Returns the name of this file.
     *
     * @return the name of this file
     */
    public final String getFileName() {
        return fileName;
    }

    public String getTempName(final AbstractBuild<?, ?> owner) {
        if (fileName != null) {
            return owner.getRootDir().getAbsolutePath() + "/" + WORKSPACE_FILES + "/" + Integer.toHexString(fileName.hashCode()) + ".tmp";
        }
        return StringUtils.EMPTY;
    }

    public boolean isSourceIgnored() {
        return sourceIgnored;
    }

    public void setSourceIgnored(boolean sourceIgnored) {
        this.sourceIgnored = sourceIgnored;
    }
}
