package com.thalesgroup.hudson.plugins.cppcheck;
import hudson.FilePath;
import hudson.Util;

import java.io.File;

public abstract class AbstractWorkspaceTest {

    protected File parentFile;
    protected FilePath workspace;

    public void createWorkspace() throws Exception {
        parentFile = Util.createTempDir();
        workspace = new FilePath(parentFile);
        if (workspace.exists()) {
            workspace.deleteRecursive();
        }
        workspace.mkdirs();
    }

    public void deleteWorkspace() throws Exception {
        workspace.deleteRecursive();
    }
}
