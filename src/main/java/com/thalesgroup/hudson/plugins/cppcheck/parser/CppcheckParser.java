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

package com.thalesgroup.hudson.plugins.cppcheck.parser;

import com.thalesgroup.hudson.plugins.cppcheck.CppcheckReport;
import com.thalesgroup.hudson.plugins.cppcheck.exception.CppcheckException;
import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckFile;
import com.thalesgroup.jenkinsci.plugins.cppcheck.model.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CppcheckParser implements Serializable {

    private static final long serialVersionUID = 1L;

    public CppcheckReport parse(final File file) throws IOException {

        if (file == null) {
            throw new IllegalArgumentException("File input is mandatory.");
        }

        if (!file.exists()) {
            throw new IllegalArgumentException("File input " + file.getName() + " must exist.");
        }


        CppcheckReport cppCheckReport = new CppcheckReport();
        List<CppcheckFile> everyErrors = new ArrayList<CppcheckFile>();
        List<CppcheckFile> styleSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> possibleStyleSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> errorSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> possibleErrorSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> noCategorySeverities = new ArrayList<CppcheckFile>();

        try {
            AtomicReference<JAXBContext> jc;
            jc = new AtomicReference<JAXBContext>();
            jc.set(JAXBContext.newInstance(com.thalesgroup.jenkinsci.plugins.cppcheck.model.Error.class, Results.class));
            Unmarshaller unmarshaller = jc.get().createUnmarshaller();
            Results results = (Results) unmarshaller.unmarshal(file);

            CppcheckFile cppcheckFile;
            for (int i = 0; i < results.getError().size(); i++) {
                com.thalesgroup.jenkinsci.plugins.cppcheck.model.Error error = results.getError().get(i);
                cppcheckFile = new CppcheckFile();

                cppcheckFile.setFileName(error.getFile());

                //line can be optional
                String lineAtr;
                if ((lineAtr = error.getLine()) != null) {
                    cppcheckFile.setLineNumber(Integer.parseInt(lineAtr));
                }

                cppcheckFile.setCppCheckId(error.getId());
                cppcheckFile.setSeverity(error.getSeverity());
                cppcheckFile.setMessage(error.getMsg());

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
                everyErrors.add(cppcheckFile);
            }

        } catch (JAXBException jaxbe) {
            throw new CppcheckException("Can't parse cppcheck result file " + file.getPath(), jaxbe);
        }

        cppCheckReport.setEverySeverities(everyErrors);
        cppCheckReport.setPossibleErrorSeverities(possibleErrorSeverities);
        cppCheckReport.setStyleSeverities(styleSeverities);
        cppCheckReport.setPossibleStyleSeverities(possibleStyleSeverities);
        cppCheckReport.setErrorSeverities(errorSeverities);
        cppCheckReport.setNoCategorySeverities(noCategorySeverities);

        return cppCheckReport;

    }
}
