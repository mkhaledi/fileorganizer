package com.mkh;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileOrganizer {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("There should be 2 arguments.");
        }
        FileOrganizer fo = new FileOrganizer();

        try {
            File srcDir = new File(args[0]);
            fo.checkSrcDirectory(srcDir);
            File destDir = fo.createDestinationDirectory(args[1]);
            fo.processFiles(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileCreationDate(File afile) throws IOException {
        Path path = Paths.get(afile.getAbsolutePath());
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        FileTime creationTime = attr.creationTime();
        Date date = new Date(creationTime.toMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMM");
        return sdf.format(date);
    }

    private void processFiles(File srcDir, File destDir) throws IOException {
        File[] listFiles = srcDir.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                processFiles(file, destDir);
            } else {
                processAFile(destDir, file);
            }
        }

    }

    private void processAFile(File destDir, File currFile) throws IOException {
        String fileCreationDate = getFileCreationDate(currFile);
        File currDir = createDirectoryIfNotExist(destDir, fileCreationDate);
        FileUtils.copyFileToDirectory(currFile, currDir);
    }

    private File createDirectoryIfNotExist(File destDir, String dirName) {
        File currentDir = new File(destDir, dirName);
        if (!currentDir.exists()) {
            currentDir.mkdirs();
        }
        return currentDir;
    }

    private void checkSrcDirectory(File srcDir) throws IOException {
        if (!srcDir.exists()) {
            throw new IOException(srcDir.getAbsolutePath() + " does not exist!");
        }
        if (!srcDir.isDirectory()) {
            throw new IOException(srcDir.getAbsolutePath() + " is not directory!");
        }
    }

    private File createDestinationDirectory(String destStr) throws IOException {
        File destDir = new File(destStr);
        if (destDir.exists()) {
            FileUtils.deleteDirectory(destDir);
        }
        destDir.mkdirs();
        return destDir;
    }
}
