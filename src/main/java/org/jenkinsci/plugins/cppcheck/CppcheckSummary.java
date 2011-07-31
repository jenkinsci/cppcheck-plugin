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
            summary.append("<a href=\"" + com.thalesgroup.hudson.plugins.cppcheck.CppcheckBuildAction.URL_NAME + "\">");

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
     * @param result the cppcheck result object
     * @return the HTML fragment representing the cppcheck report details summary
     */
    public static String createReportSummaryDetails(CppcheckResult result) {

        StringBuilder builder = new StringBuilder();
        int nbNewErrors = result.getNumberNewErrorsFromPreviousBuild();

        builder.append("<li>");

        if (nbNewErrors == 0) {
            builder.append(Messages.cppcheck_ResultAction_Detail_NoNewError());
        } else if (nbNewErrors == 1) {
            builder.append(Messages.cppcheck_ResultAction_Detail_NewOneError());
        } else {
            builder.append(Messages.cppcheck_ResultAction_Detail_NewMultipleErrors());
            builder.append(": ");
            builder.append(nbNewErrors);
        }
        builder.append("</li>");

        return builder.toString();
    }

}
