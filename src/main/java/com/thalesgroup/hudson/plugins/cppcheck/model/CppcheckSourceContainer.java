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

import com.thalesgroup.hudson.plugins.cppcheck.util.CppcheckLogger;
import hudson.FilePath;
import hudson.model.BuildListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CppcheckSourceContainer {


    private Map<Integer, CppcheckWorkspaceFile> internalMap = new HashMap<Integer, CppcheckWorkspaceFile>();

    public CppcheckSourceContainer(BuildListener listener, FilePath basedir, List<CppcheckFile> files) throws IOException, InterruptedException {
        for (CppcheckFile cppcheckFile : files) {

            CppcheckWorkspaceFile cppcheckWorkspaceFile = new CppcheckWorkspaceFile();

            String cppcheckFileName = cppcheckFile.getFileName();
            if (cppcheckFileName == null) {
                cppcheckWorkspaceFile.setFileName(null);
                cppcheckWorkspaceFile.setSourceIgnored(false);
            } else {
                FilePath sourceFilePath = new FilePath(basedir, cppcheckFileName);
                if (!sourceFilePath.exists()) {
                    CppcheckLogger.log(listener, "[WARNING] - The source file '" + sourceFilePath.toURI() + "' doesn't exist on the slave. The ability to display its source code has been removed.");
                    cppcheckWorkspaceFile.setSourceIgnored(true);
                    cppcheckWorkspaceFile.setFileName(null);
                } else if (sourceFilePath.isDirectory()) {
                    cppcheckWorkspaceFile.setFileName(sourceFilePath.getRemote());
                    cppcheckWorkspaceFile.setSourceIgnored(true);
                } else {
                    cppcheckWorkspaceFile.setFileName(sourceFilePath.getRemote());
                    cppcheckWorkspaceFile.setSourceIgnored(false);
                }
            }

            cppcheckWorkspaceFile.setCppcheckFile(cppcheckFile);
            internalMap.put(cppcheckFile.getKey(), cppcheckWorkspaceFile);
        }
    }


    public Map<Integer, CppcheckWorkspaceFile> getInternalMap() {
        return internalMap;
    }
}
