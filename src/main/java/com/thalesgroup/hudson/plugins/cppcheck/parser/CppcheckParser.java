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
import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckFile;
import org.jenkinsci.plugins.cppcheck.model.Errors;
import org.jenkinsci.plugins.cppcheck.model.Results;

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


        CppcheckReport report;
        AtomicReference<JAXBContext> jc = new AtomicReference<JAXBContext>();
        try {
            jc.set(JAXBContext.newInstance(
                    org.jenkinsci.plugins.cppcheck.model.Error.class,
                    org.jenkinsci.plugins.cppcheck.model.Errors.class,
                    org.jenkinsci.plugins.cppcheck.model.Cppcheck.class,
                    org.jenkinsci.plugins.cppcheck.model.Results.class));
            Unmarshaller unmarshaller = jc.get().createUnmarshaller();
            org.jenkinsci.plugins.cppcheck.model.Results results = (org.jenkinsci.plugins.cppcheck.model.Results) unmarshaller.unmarshal(file);
            if (results.getCppcheck() == null) {
                throw new JAXBException("Test with versio 1");
            }
            report = getReportVersion2(results);
        } catch (JAXBException jxe) {
            try {
                jc.set(JAXBContext.newInstance(com.thalesgroup.jenkinsci.plugins.cppcheck.model.Error.class, com.thalesgroup.jenkinsci.plugins.cppcheck.model.Results.class));
                Unmarshaller unmarshaller = jc.get().createUnmarshaller();
                com.thalesgroup.jenkinsci.plugins.cppcheck.model.Results results = (com.thalesgroup.jenkinsci.plugins.cppcheck.model.Results) unmarshaller.unmarshal(file);
                report = getReportVersion1(results);
            } catch (JAXBException jxe1) {
                throw new IOException(jxe1);
            }

        }
        return report;
    }

    private CppcheckReport getReportVersion1(com.thalesgroup.jenkinsci.plugins.cppcheck.model.Results results) {

        CppcheckReport cppCheckReport = new CppcheckReport();
        List<CppcheckFile> everyErrors = new ArrayList<CppcheckFile>();
        List<CppcheckFile> styleSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> possibleStyleSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> errorSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> possibleErrorSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> noCategorySeverities = new ArrayList<CppcheckFile>();

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

        cppCheckReport.setEverySeverities(everyErrors);
        cppCheckReport.setPossibleErrorSeverities(possibleErrorSeverities);
        cppCheckReport.setStyleSeverities(styleSeverities);
        cppCheckReport.setPossibleStyleSeverities(possibleStyleSeverities);
        cppCheckReport.setErrorSeverities(errorSeverities);
        cppCheckReport.setNoCategorySeverities(noCategorySeverities);

        return cppCheckReport;
    }

    private CppcheckReport getReportVersion2(Results results) {

        CppcheckReport cppCheckReport = new CppcheckReport();
        List<CppcheckFile> everyErrors = new ArrayList<CppcheckFile>();
        List<CppcheckFile> styleSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> possibleStyleSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> errorSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> possibleErrorSeverities = new ArrayList<CppcheckFile>();
        List<CppcheckFile> noCategorySeverities = new ArrayList<CppcheckFile>();

        CppcheckFile cppcheckFile;

        Errors errors = results.getErrors();

        if (errors != null) {
            for (int i = 0; i < errors.getError().size(); i++) {
                org.jenkinsci.plugins.cppcheck.model.Error error = errors.getError().get(i);
                cppcheckFile = new CppcheckFile();

                //FileName and Line
                org.jenkinsci.plugins.cppcheck.model.Error.Location location = error.getLocation();
                if (location != null) {
                    cppcheckFile.setFileName(location.getFile());
                    String lineAtr;
                    if ((lineAtr = location.getLine()) != null) {
                        cppcheckFile.setLineNumber(Integer.parseInt(lineAtr));
                    }
                }

                cppcheckFile.setCppCheckId(error.getId());
                cppcheckFile.setSeverity(error.getSeverity());
                cppcheckFile.setMessage(error.getMsg());

                if ("possible error".equals(cppcheckFile.getSeverity())) {
                    possibleErrorSeverities.add(cppcheckFile);
                } else if ("warning".equals(cppcheckFile.getSeverity())) {
                    possibleErrorSeverities.add(cppcheckFile);
                } else if ("style".equals(cppcheckFile.getSeverity())) {
                    styleSeverities.add(cppcheckFile);
                } else if ("possible style".equals(cppcheckFile.getSeverity())) {
                    possibleStyleSeverities.add(cppcheckFile);
                } else if ("information".equals(cppcheckFile.getSeverity())) {
                    possibleStyleSeverities.add(cppcheckFile);
                } else if ("error".equals(cppcheckFile.getSeverity())) {
                    errorSeverities.add(cppcheckFile);
                } else {
                    noCategorySeverities.add(cppcheckFile);
                }
                everyErrors.add(cppcheckFile);
            }
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
