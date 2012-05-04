package org.jenkinsci.plugins.cppcheck;


import com.thalesgroup.hudson.plugins.cppcheck.Messages;

/**
 * @author Gregory Boissinot
 */
public class CppcheckSummary {

    private CppcheckSummary() {
    }


    /**
     * Creates an HTML Cppcheck summary.
     *
     * @param result the cppcheck result object
     * @return the HTML fragment representing the cppcheck report summary
     */
    public static String createReportSummary(CppcheckResult result) {

        StringBuilder summary = new StringBuilder();
        int nbErrors = result.getReport().getAllErrors().size();

        summary.append(Messages.cppcheck_Errors_ProjectAction_Name());
        summary.append(": ");
        if (nbErrors == 0) {
            summary.append(Messages.cppcheck_ResultAction_NoError());
        } else {
            summary.append("<a href=\"" + CppcheckBuildAction.URL_NAME + "\">");

            if (nbErrors == 1) {
                summary.append(Messages.cppcheck_ResultAction_OneError());
            } else {
                summary.append(Messages.cppcheck_ResultAction_MultipleErrors(nbErrors));
            }
            summary.append("</a>");
        }
        summary.append(".");

        return summary.toString();
    }


    /**
     * Creates an HTML Cppcheck detailed summary.
     *
     * @param cppcheckResult the cppcheck result object
     * @return the HTML fragment representing the cppcheck report details summary
     */
    public static String createReportSummaryDetails(CppcheckResult cppcheckResult) {

        if (cppcheckResult == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        CppcheckResult previousCppcheckResult = cppcheckResult.getPreviousResult();

        CppcheckReport cppcheckReport = cppcheckResult.getReport();
        CppcheckReport previousCppcheckReport = previousCppcheckResult.getReport();

        builder.append("<li>");
        int nbNewErrorSeverity = getNbNew(cppcheckReport.getNumberErrorSeverity(), previousCppcheckReport.getNumberErrorSeverity());
        builder.append(String.format("Severity Error (nb/new) : %s/+%s", cppcheckReport.getNumberErrorSeverity(), nbNewErrorSeverity));
        builder.append("</li>");

        builder.append("<li>");
        int nbNewWarningSeverity = getNbNew(cppcheckReport.getNumberWarningSeverity(), previousCppcheckReport.getNumberWarningSeverity());
        builder.append(String.format("Severity Warning (nb/new) : %s/+%s", cppcheckReport.getNumberWarningSeverity(), nbNewWarningSeverity));
        builder.append("</li>");

        builder.append("<li>");
        int nbNewStyleSeverity = getNbNew(cppcheckReport.getNumberStyleSeverity(), previousCppcheckReport.getNumberStyleSeverity());
        builder.append(String.format("Severity Style (nb/new) : %s/+%s", cppcheckReport.getNumberStyleSeverity(), nbNewStyleSeverity));
        builder.append("</li>");

        builder.append("<li>");
        int nbNewPerformanceSeverity = getNbNew(cppcheckReport.getNumberPerformanceSeverity(), previousCppcheckReport.getNumberPerformanceSeverity());
        builder.append(String.format("Severity Performance (nb/new) : %s/+%s", cppcheckReport.getNumberPerformanceSeverity(), nbNewPerformanceSeverity));
        builder.append("</li>");

        builder.append("<li>");
        int nbNewInformationSeverity = getNbNew(cppcheckReport.getNumberInformationSeverity(), previousCppcheckReport.getNumberInformationSeverity());
        builder.append(String.format("Severity Information (nb/new) : %s/+%s", cppcheckReport.getNumberInformationSeverity(), nbNewInformationSeverity));
        builder.append("</li>");

        builder.append("<li>");
        int nbNewNoCategorySeverity = getNbNew(cppcheckReport.getNumberNoCategorySeverity(), previousCppcheckReport.getNumberNoCategorySeverity());
        builder.append(String.format("Severity Unknown (nb/new) : %s/%s", cppcheckReport.getNumberNoCategorySeverity(), nbNewNoCategorySeverity));
        builder.append("</li>");

        return builder.toString();
    }

    private static int getNbNew(int numberError, int numberPreviousError) {
        int diff = numberError - numberPreviousError;
        return (diff > 0) ? diff : 0;
    }

}
