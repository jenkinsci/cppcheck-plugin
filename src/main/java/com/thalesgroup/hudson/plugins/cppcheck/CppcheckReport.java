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

package com.thalesgroup.hudson.plugins.cppcheck;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckFile;

@ExportedBean
public class CppcheckReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<CppcheckFile> everySeverities = new ArrayList<CppcheckFile>();

    private List<CppcheckFile> errorSeverities = new ArrayList<CppcheckFile>();

    private List<CppcheckFile> possibleErrorSeverities = new ArrayList<CppcheckFile>();

    private List<CppcheckFile> styleSeverities = new ArrayList<CppcheckFile>();

    private List<CppcheckFile> possibleStyleSeverities = new ArrayList<CppcheckFile>();

    private List<CppcheckFile> noCategorySeverities = new ArrayList<CppcheckFile>();

    @Exported
    public List<CppcheckFile> getEverySeverities() {
        return everySeverities;
    }

    public List<CppcheckFile> getPossibleErrorSeverities() {
        return possibleErrorSeverities;
    }

    public void setPossibleErrorSeverities(List<CppcheckFile> possibleErrorSeverities) {
        this.possibleErrorSeverities = possibleErrorSeverities;
    }

    public List<CppcheckFile> getStyleSeverities() {
        return styleSeverities;
    }

    public void setStyleSeverities(List<CppcheckFile> styleSeverities) {
        this.styleSeverities = styleSeverities;
    }

    public List<CppcheckFile> getPossibleStyleSeverities() {
        return possibleStyleSeverities;
    }

    public void setPossibleStyleSeverities(List<CppcheckFile> possibleStyleSeverities) {
        this.possibleStyleSeverities = possibleStyleSeverities;
    }

    public List<CppcheckFile> getErrorSeverities() {
        return errorSeverities;
    }

    public void setErrorSeverities(List<CppcheckFile> errorSeverities) {
        this.errorSeverities = errorSeverities;
    }

    public List<CppcheckFile> getNoCategorySeverities() {
        return noCategorySeverities;
    }

    public void setNoCategorySeverities(List<CppcheckFile> noCategorySeverities) {
        this.noCategorySeverities = noCategorySeverities;
    }

    public void setEverySeverities(List<CppcheckFile> everySeverities) {
        this.everySeverities = everySeverities;
    }

    @Exported
    public int getNumberTotal() {
        return (everySeverities == null) ? 0 : everySeverities.size();
    }

    @Exported
    public int getNumberSeverityStyle() {
        return (styleSeverities == null) ? 0 : styleSeverities.size();
    }

    @Exported
    public int getNumberSeverityPossibleStyle() {
        return (possibleStyleSeverities == null) ? 0 : possibleStyleSeverities.size();
    }

    @Exported
    public int getNumberSeverityError() {
        return (errorSeverities == null) ? 0 : errorSeverities.size();
    }

    @Exported
    public int getNumberSeverityPossibleError() {
        return (possibleErrorSeverities == null) ? 0 : possibleErrorSeverities.size();
    }

    @Exported
    public int getNumberSeverityNoCategory() {
        return (noCategorySeverities == null) ? 0 : noCategorySeverities.size();
    }


    // Backward compatibility. Do not remove.
    // CPPCHECK:OFF
    @Deprecated
    private transient Map<Integer, CppcheckFile> internalMap;

    /**
     * Initializes members that were not present in previous versions of this plug-in.
     *
     * @return the created object
     */
    private Object readResolve() {

        if (internalMap != null) {
            for (Map.Entry<Integer, CppcheckFile> entry : internalMap.entrySet()) {

                CppcheckFile cppcheckFile = entry.getValue();
                if ("possible error".equals(cppcheckFile.getSeverity())) {
                    possibleErrorSeverities.add(cppcheckFile);
                } else if ("style".equals(cppcheckFile.getSeverity())) {
                    styleSeverities.add(cppcheckFile);
                } else if ("possible style".equals(cppcheckFile.getSeverity())) {
                    possibleStyleSeverities.add(cppcheckFile);
                } else if ("error".equals(cppcheckFile.getSeverity())) {
                    errorSeverities.add(cppcheckFile);
                } else {
                    noCategorySeverities.add(cppcheckFile);
                }
                everySeverities.add(cppcheckFile);
            }
        }

        return this;
    }


}
