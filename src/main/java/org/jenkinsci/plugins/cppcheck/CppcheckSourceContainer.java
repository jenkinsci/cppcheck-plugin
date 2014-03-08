package org.jenkinsci.plugins.cppcheck;

import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckFile;
import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckWorkspaceFile;
import hudson.FilePath;
import hudson.model.BuildListener;
import org.jenkinsci.plugins.cppcheck.util.CppcheckLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class CppcheckSourceContainer {

    private Map<Integer, CppcheckWorkspaceFile> internalMap = new HashMap<Integer, CppcheckWorkspaceFile>();

    public CppcheckSourceContainer(Map<Integer, CppcheckWorkspaceFile> internalMap) {
        this.internalMap = internalMap;
    }

    public CppcheckSourceContainer(BuildListener listener,
                                   FilePath workspace,
                                   FilePath scmRootDir,
                                   List<CppcheckFile> files) throws IOException, InterruptedException {

        int key = 1;
        for (CppcheckFile cppcheckFile : files) {
            CppcheckWorkspaceFile cppcheckWorkspaceFile = getCppcheckWorkspaceFile(listener, workspace, scmRootDir, cppcheckFile);
            //The key must be unique for all the files/errors through the merge
            cppcheckFile.setKey(key);
            cppcheckWorkspaceFile.setCppcheckFile(cppcheckFile);
            internalMap.put(key, cppcheckWorkspaceFile);
            ++key;
        }
    }

    private CppcheckWorkspaceFile getCppcheckWorkspaceFile(BuildListener listener,
                                                           FilePath workspace,
                                                           FilePath scmRootDir,
                                                           CppcheckFile cppcheckFile) throws IOException, InterruptedException {

        String cppcheckFileName = cppcheckFile.getFileName();

        if (cppcheckFileName == null) {
            CppcheckWorkspaceFile cppcheckWorkspaceFile = new CppcheckWorkspaceFile();
            cppcheckWorkspaceFile.setFileName(null);
            cppcheckWorkspaceFile.setSourceIgnored(true);
            return cppcheckWorkspaceFile;
        }

        CppcheckWorkspaceFile cppcheckWorkspaceFile = new CppcheckWorkspaceFile();
        FilePath sourceFilePath = getSourceFile(workspace, scmRootDir, cppcheckFileName);
        if (!sourceFilePath.exists()) {
            CppcheckLogger.log(listener, "[WARNING] - The source file '" + sourceFilePath.toURI() + "' doesn't exist on the slave. The ability to display its source code has been removed.");
            cppcheckWorkspaceFile.setFileName(null);
            cppcheckWorkspaceFile.setSourceIgnored(true);
        } else if (sourceFilePath.isDirectory()) {
            cppcheckWorkspaceFile.setFileName(sourceFilePath.getRemote());
            cppcheckWorkspaceFile.setSourceIgnored(true);
        } else {
            cppcheckWorkspaceFile.setFileName(sourceFilePath.getRemote());
            cppcheckWorkspaceFile.setSourceIgnored(false);
        }

        return cppcheckWorkspaceFile;
    }

    private FilePath getSourceFile(FilePath workspace, FilePath scmRootDir, String cppcheckFileName) throws IOException, InterruptedException {
        FilePath sourceFilePath = new FilePath(scmRootDir, cppcheckFileName);
        if (!sourceFilePath.exists()) {
            //try from workspace
            sourceFilePath = new FilePath(workspace, cppcheckFileName);
        }
        return sourceFilePath;
    }


    public Map<Integer, CppcheckWorkspaceFile> getInternalMap() {
        return internalMap;
    }

}
