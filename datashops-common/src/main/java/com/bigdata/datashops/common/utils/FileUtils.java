package com.bigdata.datashops.common.utils;

import static com.bigdata.datashops.common.Constants.DATA_BASEDIR_PATH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

public class FileUtils {
    public static final String DATA_BASEDIR = PropertyUtils.getString(DATA_BASEDIR_PATH, "/tmp/datashops");

    public static String getProcessExecDir(int projectId, int jobId) {
        String fileName = String.format("%s/exec/process/%s/%s", DATA_BASEDIR, projectId, jobId);
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return fileName;
    }

    public static String getJobExecLogDir() {
        String fileName = String.format("%s/log/", DATA_BASEDIR);
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return fileName;
    }

    public static void writeStringToFile(File file, String data, Charset encoding, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            IOUtils.write(data, out, encoding);
            out.close();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static void writeStringToFile(File file, String data, Charset encoding) throws IOException {
        writeStringToFile(file, data, encoding, false);
    }

    public static void writeStringToFile(File file, String data) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), false);
    }

    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("Directory '" + parent + "' could not be created");
            }
        }
        return new FileOutputStream(file, append);
    }

    public static void deleteFile(String filename) throws IOException {
        org.apache.commons.io.FileUtils.forceDelete(new File(filename));
    }
}
