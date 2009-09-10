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
package com.thalesgroup.hudson.plugins.cppcheck.util;

import java.io.IOException;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.HealthReportingAction;
import hudson.model.Result;

import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public abstract class AbstractCppcheckBuildAction extends Actionable implements Action, HealthReportingAction, StaplerProxy {


    protected AbstractBuild<?, ?> owner;

    protected AbstractCppcheckBuildAction(AbstractBuild<?, ?> owner) {
        this.owner = owner;
    }

    public <T extends AbstractCppcheckBuildAction> T getPreviousResult() {
        AbstractBuild<?, ?> b = owner;
        while (true) {
            b = b.getPreviousBuild();
            if (b == null)
                return null;
            if (b.getResult() == Result.FAILURE)
                continue;
            AbstractCppcheckBuildAction r = b.getAction(this.getClass());
            if (r != null)
                return (T) r;
        }
    }

    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }

    public abstract void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException;

}
