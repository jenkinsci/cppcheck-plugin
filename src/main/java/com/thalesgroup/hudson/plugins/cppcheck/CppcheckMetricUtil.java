package com.thalesgroup.hudson.plugins.cppcheck;

import org.apache.commons.lang.StringUtils;

public class CppcheckMetricUtil {

	public static int convert(String threshold) {
        if (isValid(threshold)) {
            if (StringUtils.isNotBlank(threshold)) {
                try {
                    return Integer.valueOf(threshold);
                }
                catch (NumberFormatException exception) {
                    // not valid
                }
            }
        }
        throw new IllegalArgumentException("Not a parsable integer value >= 0: " + threshold);
    }

    public static  boolean isValid(final String threshold) {
        if (StringUtils.isNotBlank(threshold)) {
            try {
                return Integer.valueOf(threshold) >= 0;
            }
            catch (NumberFormatException exception) {
                // not valid
            }
        }
        return false;
    }
    
    public static String getMessageSelectedSeverties(CppcheckConfig cppcheckConfig){
    	StringBuffer sb=new StringBuffer();
    	
    	if (cppcheckConfig.isAllSeverities()){
    		sb.append("with all severities");
    		return sb.toString();
    	}
    	
    	if (cppcheckConfig.isSeverityError()){
    		sb.append(" and ");
    		sb.append("severity 'error'");
    	}

    	if (cppcheckConfig.isSeverityPossibleError()){
    		sb.append(" and ");
    		sb.append("severity 'possible error'");
    	}

    	
    	if (cppcheckConfig.isSeverityPossibleStyle()){
    		sb.append(" and ");
    		sb.append("severity 'possible style'");
    	}

    	
    	if (cppcheckConfig.isSeverityStyle()){
    		sb.append(" and ");
    		sb.append("severity 'style'");
    	}

    	if (sb.length()!=0)
    		sb.delete(0, 5);
    	
    	return sb.toString();
    }
    
}
