/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.paypal;

import com.junbo.payment.common.exception.AppServerExceptions;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * SFTP Downloader.
 */
public class SFTPDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SFTPDownloader.class);
    private static final int RETRY_COUNT = 3;
    private static final int RETRY_SLEEP_SECONDS = 25000;
    private String hostName;
    private String username;
    private String password;

    public void download(String localFilePath, String remoteFilePath){
        int retryTime = 0;
        Exception ex = null;
        while(retryTime < RETRY_COUNT){
            LOGGER.info("download file " + remoteFilePath + " for times: " + retryTime);
            StandardFileSystemManager manager = new StandardFileSystemManager();
            try {
                manager.init();
                String downloadFilePath = localFilePath;
                FileObject localFile = manager.resolveFile(downloadFilePath);
                FileObject remoteFile = manager.resolveFile(
                        getConnection(remoteFilePath), getFileOptions());
                localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);
                LOGGER.info("download file successfully!");
                return;
            } catch (Exception e) {
                LOGGER.warn("download file failed:" + e.toString());
                ex = e;
            } finally {
                manager.close();
            }
            retryTime++;
            sleep();

        }
        if(ex != null){
            throw AppServerExceptions.INSTANCE.errorDownloadFile(remoteFilePath).exception();
        }
    }

    public String[] getFileLists(String remoteFilePath){
        int retryTime = 0;
        Exception ex = null;
        while(retryTime < RETRY_COUNT){
            LOGGER.info("download file " + remoteFilePath + " for times: " + retryTime);
            StandardFileSystemManager manager = new StandardFileSystemManager();
            try {
                manager.init();
                FileObject remoteFile = manager.resolveFile(getConnection(remoteFilePath), getFileOptions());
                FileObject[] files = remoteFile.getChildren();
                ArrayList<String> fileNames = new ArrayList<String>();
                for(FileObject file : files){
                    String filename = file.getName().getPath();
                    String fileShortName = filename.substring(filename.lastIndexOf("/") + 1, filename.length());
                    fileNames.add(fileShortName);
                }
                return fileNames.toArray(new String[0]);
            } catch (Exception e) {
                LOGGER.warn("Failed to find files on sftp. " + e.toString());
                ex = e;
            } finally {
                manager.close();
            }
            retryTime++;
            sleep();
        }
        if(ex != null){
            throw AppServerExceptions.INSTANCE.errorDownloadFile(remoteFilePath).exception();
        }
        return null;
    }

    private String getConnection(String remoteFilePath) {
        return "sftp://" + username + ":" + password + "@" + hostName + "/" + remoteFilePath;
    }

    private FileSystemOptions getFileOptions() throws FileSystemException{
        FileSystemOptions opts = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);
        return opts;
    }

    private void sleep() {
        //sleep for a while before next retry
        try {
            Thread.sleep(RETRY_SLEEP_SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("error occurred when sleep");
        }
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
