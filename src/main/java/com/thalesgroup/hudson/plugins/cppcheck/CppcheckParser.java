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

import hudson.AbortException;
import hudson.FilePath;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckFile;

public class CppcheckParser implements FilePath.FileCallable<CppcheckReport> {

	private static final long serialVersionUID = 1L;

	private FilePath resultFilePath;
	private static final Logger LOGGER = Logger.getLogger(CppcheckParser.class.getName());

	public CppcheckParser() {
		resultFilePath = null;
	}

	public CppcheckParser(FilePath resultFilePath) {
		this.resultFilePath = resultFilePath;
	}

	public CppcheckReport invoke(java.io.File workspace, VirtualChannel channel) throws IOException {

		CppcheckReport cppCheckReport = new CppcheckReport();

		Document document = null;
		try {
			SAXBuilder sxb = new SAXBuilder();
			document = sxb.build(new InputStreamReader(new FileInputStream(new File(resultFilePath.toURI())), "UTF-8"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Parsing file error :" + e.toString());
			throw new AbortException("Parsing file error");
		}
		
		Element results = document.getRootElement();
		List list = results.getChildren();	
		
        List<CppcheckFile> everyErrors = new ArrayList<CppcheckFile>();
	    List<CppcheckFile> allErrors= new ArrayList<CppcheckFile>();
        List<CppcheckFile> styleErrors= new ArrayList<CppcheckFile>();
        List<CppcheckFile> allStyleErrors= new ArrayList<CppcheckFile>();
        List<CppcheckFile> errorErrors= new ArrayList<CppcheckFile>();        
        Map<Integer, CppcheckFile> agregateMap = new HashMap<Integer, CppcheckFile>();

		CppcheckFile cppcheckFile;
		for (int i = 0; i < list.size(); i++) {
			Element elt = (Element) list.get(i);
			
			cppcheckFile = new CppcheckFile();
			
			
			cppcheckFile.setKey(i+1);
			cppcheckFile.setFileName(elt.getAttributeValue("file"));
			//line can be optional
			String lineAtr=null;
			if ((lineAtr=elt.getAttributeValue("line"))!=null){
				cppcheckFile.setLineNumber(Integer.parseInt(lineAtr));
			}
			
			cppcheckFile.setCppCheckId(elt.getAttributeValue("id"));
			cppcheckFile.setSeverity(elt.getAttributeValue("severity"));
			cppcheckFile.setMessage(elt.getAttributeValue("msg"));

            if ("all".equals(cppcheckFile.getSeverity())){
                allErrors.add(cppcheckFile);
            }
            else if ("style".equals(cppcheckFile.getSeverity())){
                styleErrors.add(cppcheckFile);
            }
            else if ("all style".equals(cppcheckFile.getSeverity())){
                allStyleErrors.add(cppcheckFile);
            }
            else if ("error".equals(cppcheckFile.getSeverity())){
                errorErrors.add(cppcheckFile);
            }
			everyErrors.add(cppcheckFile);
			
			agregateMap.put(cppcheckFile.getKey(), cppcheckFile);
		}

		cppCheckReport.setEveryErrors(everyErrors);
        cppCheckReport.setAllErrors(allErrors);
        cppCheckReport.setStyleErrors(styleErrors);
        cppCheckReport.setAllStyleErrors(allStyleErrors);
        cppCheckReport.setErrorErrors(errorErrors);
        cppCheckReport.setInternalMap(agregateMap);
              
		return cppCheckReport;
	}

	public FilePath getResultFilePath() {
		return resultFilePath;
	}

	public void setResultFilePath(FilePath resultFilePath) {
		this.resultFilePath = resultFilePath;
	}

}
