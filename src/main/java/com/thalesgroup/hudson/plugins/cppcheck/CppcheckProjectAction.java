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

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;

import com.thalesgroup.hudson.plugins.cppcheck.util.AbstractCppcheckProjectAction;


public class CppcheckProjectAction extends AbstractCppcheckProjectAction {

    public String getSearchUrl() {
        return getUrlName();
    }

    public CppcheckProjectAction(final AbstractProject<?, ?> project) {
        super(project);
    }

    public AbstractBuild<?, ?> getLastFinishedBuild() {
        AbstractBuild<?, ?> lastBuild = (AbstractBuild<?, ?>) project.getLastBuild();
        while (lastBuild != null && (lastBuild.isBuilding() || lastBuild.getAction(CppcheckBuildAction.class) == null)) {
            lastBuild = lastBuild.getPreviousBuild();
        }
        return lastBuild;
    }

    @SuppressWarnings("unused")
    public final boolean isDisplayGraph() {
        //Latest
        AbstractBuild<?, ?> b = getLastFinishedBuild();
        if (b == null){
            return false;
        }

        //Affect previous
        b = b.getPreviousBuild();
        if (b != null) {

            for (; b != null; b = b.getPreviousBuild()) {
                if (b.getResult().isWorseOrEqualTo(Result.FAILURE)) {
                    continue;
                }
                CppcheckBuildAction action = b.getAction(CppcheckBuildAction.class);
                if (action == null || action.getResult() == null) {
                    continue;
                }
                CppcheckResult result = action.getResult();
                if (result == null)
                    continue;

                return true;
            }
        }
        return false;
    }

    public Integer getLastResultBuild() {
        for (AbstractBuild<?, ?> b = (AbstractBuild<?, ?>) project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
            if (b.getResult() == Result.FAILURE)
                continue;
            CppcheckBuildAction r = b.getAction(CppcheckBuildAction.class);
            if (r != null)
                return b.getNumber();
        }
        return null;
    }


    public String getDisplayName() {
        return "Cppcheck Results";
    }

    public String getUrlName() {
        return CppcheckBuildAction.URL_NAME;
    }
}
