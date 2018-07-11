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

package org.jenkinsci.plugins.cppcheck;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import hudson.slaves.DumbSlave;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.SingleFileSCM;

import java.util.ArrayList;
import java.util.List;

public class CppcheckPublisherTest extends HudsonTestCase {


    /**
     * Verify that it works on a master.
     */
    public void testOnMaster() throws Exception {
        FreeStyleProject project = createFreeStyleProject();

        List<SingleFileSCM> files = new ArrayList<SingleFileSCM>(2);

        files.add(new SingleFileSCM("cppcheckresult/testcppcheck1.xml",
                getClass().getResource("testcppcheck1.xml")));


        project.setScm(new MultiFileSCM(files));

        project.getPublishersList().add(new CppcheckPublisher("cppcheckresult/testcppcheck1.xml", null, null, null, null, null, null, null));
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

        project.getPublishersList().add(new CppcheckPublisher("cppcheckresult/testcppcheck1.xml", null, null, null, null, null, null, null));
        FreeStyleBuild build1 = project.scheduleBuild2(0).get();
        assertBuildStatusSuccess(build1);
    }


}
