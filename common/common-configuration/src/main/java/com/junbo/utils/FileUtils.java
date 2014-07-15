/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

/**
 * Java doc.
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private FileUtils() { }

    public static void checkPermission600(Path path) {
        // check permission, it must be owner read and write only
        Set<PosixFilePermission> permissions = null;
        try {
            permissions = Files.readAttributes(path, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS).permissions();

            if (CollectionUtils.isEmpty(permissions)) {
                throw new IllegalAccessException("Permission check invalid.");
            }

            // Only support OWNER_READ and OWNER_WRITE
            if (permissions.size() > 2) {
                throw new IllegalAccessException("Permission only valid for OWNER_READ and OWNER_WRITE.");
            }

            for (PosixFilePermission permission : permissions) {
                if (permission != PosixFilePermission.OWNER_READ && permission != PosixFilePermission.OWNER_WRITE) {
                    throw new IllegalAccessException("Permission only valid for OWNER_READ and OWNER_WRITE.");
                }
            }
        } catch (UnsupportedOperationException unSupportedOperationEx) {
            logger.warn("Skip permission check.");
        } catch (Exception ex) {
            logger.error("Error checking permission for file: " + path);
            throw new RuntimeException(ex);
        }
    }
}
