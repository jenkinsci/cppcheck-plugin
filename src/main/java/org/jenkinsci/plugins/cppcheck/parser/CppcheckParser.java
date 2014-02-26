package org.jenkinsci.plugins.cppcheck.parser;

import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckFile;
import org.jenkinsci.plugins.cppcheck.CppcheckReport;
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

/**
 * @author Gregory Boissinot
 */
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
        List<CppcheckFile> allErrors = new ArrayList<CppcheckFile>();
        List<CppcheckFile> errorSeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> warningSeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> styleSeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> performanceSeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> informationSeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> noCategorySeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> portabilitySeverityList = new ArrayList<CppcheckFile>();

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
                warningSeverityList.add(cppcheckFile);
            } else if ("style".equals(cppcheckFile.getSeverity())) {
                styleSeverityList.add(cppcheckFile);
            } else if ("possible style".equals(cppcheckFile.getSeverity())) {
                performanceSeverityList.add(cppcheckFile);
            } else if ("error".equals(cppcheckFile.getSeverity())) {
                errorSeverityList.add(cppcheckFile);
            } else {
                noCategorySeverityList.add(cppcheckFile);
            }
            allErrors.add(cppcheckFile);
        }

        cppCheckReport.setAllErrors(allErrors);
        cppCheckReport.setErrorSeverityList(errorSeverityList);
        cppCheckReport.setInformationSeverityList(informationSeverityList);
        cppCheckReport.setNoCategorySeverityList(noCategorySeverityList);
        cppCheckReport.setPerformanceSeverityList(performanceSeverityList);
        cppCheckReport.setStyleSeverityList(styleSeverityList);
        cppCheckReport.setWarningSeverityList(warningSeverityList);
        cppCheckReport.setPortabilitySeverityList(portabilitySeverityList);


        return cppCheckReport;
    }

    private CppcheckReport getReportVersion2(Results results) {

        CppcheckReport cppCheckReport = new CppcheckReport();
        List<CppcheckFile> allErrors = new ArrayList<CppcheckFile>();
        List<CppcheckFile> errorSeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> warningSeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> styleSeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> performanceSeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> informationSeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> noCategorySeverityList = new ArrayList<CppcheckFile>();
        List<CppcheckFile> portabilitySeverityList = new ArrayList<CppcheckFile>();

        CppcheckFile cppcheckFile;

        Errors errors = results.getErrors();

        if (errors != null) {
            for (int i = 0; i < errors.getError().size(); i++) {
                org.jenkinsci.plugins.cppcheck.model.Error error = errors.getError().get(i);
                cppcheckFile = new CppcheckFile();

                cppcheckFile.setCppCheckId(error.getId());
                cppcheckFile.setSeverity(error.getSeverity());
                cppcheckFile.setMessage(error.getMsg());

                if ("warning".equals(cppcheckFile.getSeverity())) {
                    warningSeverityList.add(cppcheckFile);
                } else if ("style".equals(cppcheckFile.getSeverity())) {
                    styleSeverityList.add(cppcheckFile);
                } else if ("performance".equals(cppcheckFile.getSeverity())) {
                    performanceSeverityList.add(cppcheckFile);
                } else if ("error".equals(cppcheckFile.getSeverity())) {
                    errorSeverityList.add(cppcheckFile);
                } else if ("information".equals(cppcheckFile.getSeverity())) {
                    informationSeverityList.add(cppcheckFile);
                } else if ("portability".equals(cppcheckFile.getSeverity())) {
                    portabilitySeverityList.add(cppcheckFile);
                } else {
                    noCategorySeverityList.add(cppcheckFile);
                }
                allErrors.add(cppcheckFile);

                //FileName and Line
                org.jenkinsci.plugins.cppcheck.model.Error.Location location = error.getLocation();
                if (location != null) {
                    cppcheckFile.setFileName(location.getFile());
                    String lineAtr;
                    if ((lineAtr = location.getLine()) != null) {
                        cppcheckFile.setLineNumber(Integer.parseInt(lineAtr));
                    }
                }
            }
        }

        cppCheckReport.setAllErrors(allErrors);
        cppCheckReport.setErrorSeverityList(errorSeverityList);
        cppCheckReport.setInformationSeverityList(informationSeverityList);
        cppCheckReport.setNoCategorySeverityList(noCategorySeverityList);
        cppCheckReport.setPerformanceSeverityList(performanceSeverityList);
        cppCheckReport.setStyleSeverityList(styleSeverityList);
        cppCheckReport.setWarningSeverityList(warningSeverityList);
        cppCheckReport.setPortabilitySeverityList(portabilitySeverityList);

        if (results.getCppcheck() != null) {
            cppCheckReport.setVersion(results.getCppcheck().getVersion());
        }

        return cppCheckReport;
    }
}

