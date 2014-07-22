/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.file;

import com.junbo.payment.common.exception.AppServerExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Csv Reader.
 */
public class CsvReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessingJob.class);
    private String filePath;
    private BufferedReader reader;
    private String line;
    private Map<String, Integer> fieldIndex= new HashMap<String, Integer>();
    public CsvReader(String filePath) throws FileNotFoundException {
        this.filePath = filePath;
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
    }

    public boolean readLine() throws IOException {
        line = reader.readLine();
        return line == null;
    }

    public void close() throws IOException {
        reader.close();
    }

    public void readHeaders() throws IOException {
        boolean eof = readLine();
        if(eof){
            Integer index = 0;
            String[] fields = line.split(",");
            for(String field : fields){
                fieldIndex.put(field, index++);
            }
        }
    }

    public String getFieldValue(String fieldName){
        if(line != null){
            String[] fields = line.split(",");
            Integer index = fieldIndex.get(fieldName);
            if(index == null){
                LOGGER.error("get index error for file: "+ filePath + " with field:" + fieldName);
                throw AppServerExceptions.INSTANCE.errorParseBatchFile(filePath).exception();
            }
            if(index > fields.length){
                LOGGER.error("index outbound for file: "+ filePath + " with field:" + fieldName);
                throw AppServerExceptions.INSTANCE.errorParseBatchFile(filePath).exception();
            }
            return fields[index];
        }else{
            LOGGER.error("error parse file: "+ filePath + " for field:" + fieldName);
            throw AppServerExceptions.INSTANCE.errorParseBatchFile(filePath).exception();
        }

    }
}
