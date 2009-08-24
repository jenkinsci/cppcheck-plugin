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

import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.thalesgroup.hudson.plugins.cppcheck.config.CppcheckConfig;
import com.thalesgroup.hudson.plugins.cppcheck.util.CppcheckBuildResultEvaluator;
import com.thalesgroup.hudson.plugins.cppcheck.util.CppcheckUtil;
import com.thalesgroup.hudson.plugins.cppcheck.util.Messages;

public class CppcheckPublisher extends Publisher {
	
    private CppcheckConfig cppcheckConfig;

    @DataBoundConstructor
    public CppcheckPublisher(){    	
    }
    
    @Override
    public CppcheckDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public Action getProjectAction(AbstractProject<?,?> project){
        return new CppcheckProjectAction(project);
    }

    protected boolean canContinue(final Result result) {
        return result != Result.ABORTED && result != Result.FAILURE;
    }
   
    public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	@Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener){
    	
        if(this.canContinue(build.getResult())){      	
            Messages.log(listener,"Starting the cppcheck analysis.");
        	
            final FilePath[] moduleRoots= build.getProject().getModuleRoots();
            final boolean multipleModuleRoots= moduleRoots != null && moduleRoots.length > 1;
            final FilePath moduleRoot= multipleModuleRoots ? build.getProject().getWorkspace() : build.getProject().getModuleRoot();
        	        	
            CppcheckParserResult parser = new CppcheckParserResult(listener, cppcheckConfig.getCppcheckReportPattern());            
            CppcheckReport cppcheckReport= null;
            try{
            	cppcheckReport= moduleRoot.act(parser);            	
            }
            catch(Exception e){
            	Messages.log(listener,"Error on cppcheck analysis: " + e);
            	build.setResult(Result.FAILURE);
                return false;            
            }
            
            if (cppcheckReport == null){
            	build.setResult(Result.FAILURE);
                return false;            	
            }
            
            CppcheckResult result = new CppcheckResult(cppcheckReport, build);

            Result buildResult = new CppcheckBuildResultEvaluator().evaluateBuildResult(
                   listener, CppcheckUtil.getNumberErrors(cppcheckConfig, result, false), CppcheckUtil.getNumberErrors(cppcheckConfig, result,true),cppcheckConfig);

            if (buildResult != Result.SUCCESS) {
                build.setResult(buildResult);
            }

            CppcheckBuildAction buildAction = new CppcheckBuildAction(build, result, cppcheckConfig);
            build.addAction(buildAction);
            
            Messages.log(listener,"End of the cppcheck analysis.");
        }
        return true;
    }

    //@Extension    
    public static final CppcheckDescriptor DESCRIPTOR = new CppcheckDescriptor();

    public static final class CppcheckDescriptor extends BuildStepDescriptor<Publisher> {


        public CppcheckDescriptor() {
            super(CppcheckPublisher.class);
            load();
        }
               
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return FreeStyleProject.class.isAssignableFrom(jobType) || MatrixProject.class.isAssignableFrom(jobType);
        }

        @Override
        public String getDisplayName() {
            return "Publish Cppcheck results";
        }

        @Override
        public final String getHelpFile() {
            return getPluginRoot() + "help.html";
        }

        public String getPluginRoot() {
            return "/plugin/cppcheck/";
        }
        
        public CppcheckConfig getConfig() {
            return new CppcheckConfig();
        }
        
		@Override
		public Publisher newInstance(StaplerRequest req, JSONObject formData)
				throws hudson.model.Descriptor.FormException {
			
			CppcheckPublisher pub = new CppcheckPublisher();
			
			CppcheckConfig cppcheckConfig =  req.bindJSON(CppcheckConfig.class,formData);
			pub.setCppcheckConfig(cppcheckConfig);
			
			
			return pub;
		}
    }

	public CppcheckConfig getCppcheckConfig() {
		return cppcheckConfig;
	}

	public void setCppcheckConfig(CppcheckConfig cppcheckConfig) {
		this.cppcheckConfig = cppcheckConfig;
	}


}
