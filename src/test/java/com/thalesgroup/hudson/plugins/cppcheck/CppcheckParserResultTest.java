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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class CppcheckParserResultTest extends AbstractWorkspaceTest {

    private BuildListener listener;
    private VirtualChannel channel;

    @Before
    public void setUp() throws Exception {
        listener = mock(BuildListener.class);
        when(listener.getLogger()).thenReturn(new PrintStream(new ByteArrayOutputStream()));
        channel = mock(VirtualChannel.class);
        super.createWorkspace();
    }

    @Test
    public void testNullPattern() {
        CppcheckParserResult cppcheckParserResult = new CppcheckParserResult(listener, null);
        Assert.assertEquals("With none pattern, the default pattern must be " + CppcheckParserResult.DELAULT_REPORT_MAVEN, CppcheckParserResult.DELAULT_REPORT_MAVEN, cppcheckParserResult.getCppcheckReportPattern());
    }

    @Test
    public void testEmptyPattern() {
        CppcheckParserResult cppcheckParserResult = new CppcheckParserResult(listener, null);
        Assert.assertEquals("With empty pattern, the default pattern must be " + CppcheckParserResult.DELAULT_REPORT_MAVEN, CppcheckParserResult.DELAULT_REPORT_MAVEN, cppcheckParserResult.getCppcheckReportPattern());
    }

    @Test
    public void testNoMatch() throws Exception {
        CppcheckParserResult cppcheckParserResult = new CppcheckParserResult(listener, "*.xml");
        CppcheckReport report = cppcheckParserResult.invoke(new File(workspace.toURI()), channel);
        Assert.assertEquals("A pattern with no match files is not allowed.", null, report);
    }

}
