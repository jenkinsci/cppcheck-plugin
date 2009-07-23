package com.thalesgroup.hudson.plugins.cppcheck;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import hudson.slaves.DumbSlave;

import java.util.ArrayList;
import java.util.List;

import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.SingleFileSCM;

public class CppcheckPublisherTest  extends HudsonTestCase {


    /**
     * Verify that it works on a master.
     */
    public void testOnMaster() throws Exception {
        FreeStyleProject project = createFreeStyleProject();

        List<SingleFileSCM> files = new ArrayList<SingleFileSCM>(2);
	
        files.add(new SingleFileSCM("cppcheckresult/testcppcheck1.xml",
                                    getClass().getResource("testcppcheck1.xml")));

        
        project.setScm(new MultiFileSCM(files));
	
        project.getPublishersList().add(new CppcheckPublisher("cppcheckresult/testcppcheck1.xml", null,null,null,null,null,null,null));
        FreeStyleBuild build1 = project.scheduleBuild2(0).get();
	
        //FreeStyleBuild build2 = project.scheduleBuild2(0).get();
        System.out.println(build1.getLog());
        assertBuildStatusSuccess(build1);

    }
    
    /**
     * Verify that it works on a slave.
     */
    public void oldtestOnSlave() throws Exception {
        FreeStyleProject project = createFreeStyleProject();
        DumbSlave slave = createSlave(new Label("cppcheck-test-slave"));
        
        project.setAssignedLabel(slave.getSelfLabel());
        List<SingleFileSCM> files = new ArrayList<SingleFileSCM>(2);
        files.add(new SingleFileSCM("cppcheckresult/testcppcheck1.xml",
                getClass().getResource("testcppcheck1.xml")));       
        project.setScm(new MultiFileSCM(files));
	
        project.getPublishersList().add(new CppcheckPublisher("cppcheckresult/testcppcheck1.xml", null,null,null,null,null,null,null));
        FreeStyleBuild build1 = project.scheduleBuild2(0).get();
        assertBuildStatusSuccess(build1);
    }


	
}
