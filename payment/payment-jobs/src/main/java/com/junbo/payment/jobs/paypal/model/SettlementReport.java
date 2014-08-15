/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.paypal.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Settlement Report.
 */
public class SettlementReport {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettlementReport.class);
    private List<ReportSection> sections = new ArrayList<ReportSection>();

    public void loadReport(String[] filenames){
        if(filenames == null || filenames.length == 0){
            return ;
        }
        ReportSection section = new ReportSection();
        this.getSections().add(section);
        for(String filename : filenames){
            LOGGER.info("Loading file: " + filename);
            int lineNumber = 0;
            Reader inputStreamReader = null;
            try {
                inputStreamReader = getReader(new BufferedInputStream(new FileInputStream(filename)));
            } catch (IOException e) {
                LOGGER.error("error parse batch file :" + filename + " " + e.toString());
                continue;
            }
            BufferedReader in = new BufferedReader(inputStreamReader);
            String line = "";
            try {
                while((line = in.readLine()) != null){
                    lineNumber++;
                    String[] values = parseLine(line);
                    SectionBodyRow row;
                    try{
                        row = SectionBodyRow.getRawData(values[0], values);
                    }
                    catch(Exception e){
                        LOGGER.error("error parse batch file :" + filename + " line:" + lineNumber + " " + e.toString());
                        break;
                    }
                    // ignore the data not cared about
                    if(row == null){
                        continue;
                    }
                    section.getRows().add(row);
                }
            } catch (IOException e) {
                LOGGER.error("error parse batch file :" + filename + " " + e.toString());
                continue;
            }
        }
    }

    private Reader getReader(InputStream in) throws IOException {
        in.mark(3);
        int byte1 = in.read();
        int byte2 = in.read();
        if (byte1 == 0xFF && byte2 == 0xFE) {
            return new InputStreamReader(in, "UTF-16LE");
        } else if (byte1 == 0xFF && byte2 == 0xFF) {
            return new InputStreamReader(in, "UTF-16BE");
        } else if (byte1 == 0xFE && byte2 == 0xFF) {
            return new InputStreamReader(in, "UTF-16");
        } else {
            int byte3 = in.read();
            if (byte1 == 0xEF && byte2 == 0xBB && byte3 == 0xBF) {
                return new InputStreamReader(in, "UTF-8");
            } else {
                in.reset();
                return new InputStreamReader(in);
            }
        }
    }

    private String[] parseLine(String line) {
        List<String> fields = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        boolean inQuotation = false;
        for(char c : line.toCharArray()){
            if(inQuotation){
                sb.append(c);
                if(c == '"'){
                    inQuotation = false;
                }
            }else{
                if(c == ','){
                    append(fields, sb.toString());
                    sb = new StringBuffer();
                }else if(c == '"'){
                    inQuotation = true;
                }else{
                    sb.append(c);
                }
            }
        }
        append(fields, sb.toString());

        return fields.toArray(new String[0]);
    }

    private void append(List<String> source, String str){
        if(str.startsWith("\"")){
            str = str.substring(1);
        }
        if(str.endsWith("\"")){
            str = str.substring(0, str.length() - 1);
        }
        str = str.replace("\"\"", "");
        source.add(str);
    }

    public List<ReportSection> getSections() {
        return sections;
    }

    public void setSections(List<ReportSection> sections) {
        this.sections = sections;
    }
}
