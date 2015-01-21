package com.junbo.order.jobs.utils.ftp

import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemOptions
import org.apache.commons.vfs2.Selectors
import org.apache.commons.vfs2.impl.StandardFileSystemManager
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder

/**
 * Created by fzhang on 2015/1/18.
 */
class FTPUtils {

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

    boolean uploadFile(File localeFile, String remotePath) throws IOException {
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

    void downloadFile(File localeFile, String remotePath) throws IOException {
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
}
