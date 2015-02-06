package com.junbo.order.jobs.utils.ftp

import com.junbo.order.jobs.Constants
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemOptions
import org.apache.commons.vfs2.Selectors
import org.apache.commons.vfs2.impl.StandardFileSystemManager
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Created by fzhang on 2015/1/18.
 */
class FTPUtils {

    private Logger LOGGER = LoggerFactory.getLogger(FTPUtils)

    private final static long INITIAL_RETRY_INTERVAL_SECOND = 10

    private final static long MAX_RETRY_INTERVAL_SECOND = 300

    private final static int DEFAULT_RETRY = 5

    private String host

    private String port

    private String user

    private String password

    void setHost(String host) {
        this.host = host
    }

    void setPort(String port) {
        this.port = port
    }

    void setUser(String user) {
        this.user = user
    }

    void setPassword(String password) {
        this.password = password
    }

    boolean uploadFile(File localeFile, String remotePath) {
        return uploadFile(localeFile, remotePath, DEFAULT_RETRY)
    }

    boolean uploadFile(File localeFile, String remotePath, int maxRetry) throws IOException {
        maxRetry = Math.max(0, maxRetry)
        int retryCount = 0
        long start = System.currentTimeMillis()
        while (retryCount < maxRetry) {
            LOGGER.info('name=Start_Upload_File, file={}, retryCount={}', localeFile.getPath(), retryCount)
            try {
                if (innerUploadFile(localeFile, remotePath)) {
                    LOGGER.info('name=Finish_Upload_File, file={}, latencyInMs={}', localeFile.getPath(), System.currentTimeMillis() - start)
                } else {
                    LOGGER.info('name=FileAlreadyExistOnServer, file={}, skip', localeFile.path)
                }
                return true
            } catch (IOException ex) {
                LOGGER.error('name=UploadFileError, path={}', localeFile.path, ex)
                Thread.sleep(getInterval(retryCount) * Constants.MS_A_SECOND)
                retryCount++
            }
        }

        return false
    }

    boolean downloadFile(File localeFile, String remotePath) {
        return downloadFile(localeFile, remotePath, DEFAULT_RETRY)
    }

    boolean downloadFile(File localeFile, String remotePath, int maxRetry) throws IOException {
        int retryCount = 0
        long start = System.currentTimeMillis()
        while (retryCount < maxRetry) {
            if (localeFile.exists()) {
                localeFile.delete()
            }
            LOGGER.info('name=StartDownloadFile, localPath={}, remotePath={},retryCount={}', localeFile.path, remotePath, retryCount)
            try {
                innerDownloadFile(localeFile, remotePath)
                LOGGER.info('name=EndDownloadStatusUpdateFile,remotePath={},latencyInMs={}', remotePath, System.currentTimeMillis() - start)
                return true
            } catch (FileNotFoundException ex) {
                LOGGER.info('name=FileNotFoundOnFTP,remotePath={}', remotePath)
                break
            } catch (IOException ex) {
                LOGGER.info('name=Fail_To_Download,remotePath={}', remotePath, ex)
                Thread.sleep(getInterval(retryCount) * Constants.MS_A_SECOND)
                retryCount++
            }
        }

        return false
    }

    private boolean innerUploadFile(File localeFile, String remotePath) throws IOException {
        StandardFileSystemManager manager = new StandardFileSystemManager();
        try {
            manager.init()
            FileSystemOptions opts = getFTPFileSystemOptions();

            //Create the SFTP URI using the host name, userid, password,  remote path and file name
            String sftpUri = "sftp://${user}:${password}@${host}:${port}/${remotePath}"
            FileObject localFile = manager.resolveFile(localeFile.toURI().toString())
            FileObject remoteFile = manager.resolveFile(sftpUri, opts);
            if (remoteFile.exists()) {
                return false
            } else {
                remoteFile.copyFrom(localFile, Selectors.SELECT_SELF)
                return true
            }
        } finally {
            manager.close()
        }
    }

    private void innerDownloadFile(File localeFile, String remotePath) throws IOException {
        StandardFileSystemManager manager = new StandardFileSystemManager();
        try {
            manager.init()
            FileSystemOptions opts = getFTPFileSystemOptions();

            //Create the SFTP URI using the host name, userid, password,  remote path and file name
            String sftpUri = "sftp://${user}:${password}@${host}:${port}/${remotePath}"
            FileObject localFile = manager.resolveFile(localeFile.toURI().toString())
            FileObject remoteFile = manager.resolveFile(sftpUri, opts);
            if (!remoteFile.exists()) {
                throw new FileNotFoundException("Remote file not found:${remotePath}")
            } else {
                localFile.copyFrom(remoteFile, Selectors.SELECT_SELF)
            }
        } finally {
            manager.close()
        }
    }

    private FileSystemOptions getFTPFileSystemOptions() {
        FileSystemOptions opts = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no")
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true)
        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000)
        return opts
    }

    private int getInterval(int retryCount) {
        long div = (MAX_RETRY_INTERVAL_SECOND >> retryCount)
        return div > INITIAL_RETRY_INTERVAL_SECOND ? (INITIAL_RETRY_INTERVAL_SECOND << retryCount) : MAX_RETRY_INTERVAL_SECOND
    }
}
