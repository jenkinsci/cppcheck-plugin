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
        int nbErrors = result.getStatistics().getNumberTotal();

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

        CppcheckStatistics current = cppcheckResult.getStatistics();
        // FIXME: May be null, NPE always thrown for first build
        CppcheckStatistics previous = previousCppcheckResult.getStatistics();

        builder.append("<li>");
        int nbNewErrorSeverity = getNbNew(current.getNumberErrorSeverity(), previous.getNumberErrorSeverity());
        builder.append(String.format("Severity Error (nb/new) : %s/+%s", current.getNumberErrorSeverity(), nbNewErrorSeverity));
        builder.append("</li>");

        builder.append("<li>");
        int nbNewWarningSeverity = getNbNew(current.getNumberWarningSeverity(), previous.getNumberWarningSeverity());
        builder.append(String.format("Severity Warning (nb/new) : %s/+%s", current.getNumberWarningSeverity(), nbNewWarningSeverity));
        builder.append("</li>");

        builder.append("<li>");
        int nbNewStyleSeverity = getNbNew(current.getNumberStyleSeverity(), previous.getNumberStyleSeverity());
        builder.append(String.format("Severity Style (nb/new) : %s/+%s", current.getNumberStyleSeverity(), nbNewStyleSeverity));
        builder.append("</li>");

        builder.append("<li>");
        int nbNewPerformanceSeverity = getNbNew(current.getNumberPerformanceSeverity(), previous.getNumberPerformanceSeverity());
        builder.append(String.format("Severity Performance (nb/new) : %s/+%s", current.getNumberPerformanceSeverity(), nbNewPerformanceSeverity));
        builder.append("</li>");

        builder.append("<li>");
        int nbNewInformationSeverity = getNbNew(current.getNumberInformationSeverity(), previous.getNumberInformationSeverity());
        builder.append(String.format("Severity Information (nb/new) : %s/+%s", current.getNumberInformationSeverity(), nbNewInformationSeverity));
        builder.append("</li>");

        builder.append("<li>");
        int nbNewNoCategorySeverity = getNbNew(current.getNumberNoCategorySeverity(), previous.getNumberNoCategorySeverity());
        builder.append(String.format("Severity Unknown (nb/new) : %s/%s", current.getNumberNoCategorySeverity(), nbNewNoCategorySeverity));
        builder.append("</li>");

        return builder.toString();
    }

    private static int getNbNew(int numberError, int numberPreviousError) {
        int diff = numberError - numberPreviousError;

        // TODO: This is probably incorrect
        return (diff > 0) ? diff : 0;
    }

}
