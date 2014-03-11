/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.ds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by minhao on 3/8/14.
 */
public class ShardDataSourceMapper {
    private static final String DATA_SOURCE = "datasource";
    private static final String DATA_SOURCE_ID = "id";
    private static final String DATA_SOURCE_ENABLED = "enabled";
    private static final String DATA_SOURCE_SHARDID_RANGE = "shardIdRange";
    private static final String DATA_SOURCE_JDBC_URL_TEMPLATE = "jdbcUrlTemplate";

    private static Logger logger = LoggerFactory.getLogger(ShardDataSourceMapper.class);

    private Map<Integer, DataSourceConfig> dataSourceConfigMap;

    public void setDataSourceMapping(String dataSourceMapping) {
        this.dataSourceConfigMap = buildDataSourceMap(new ByteArrayInputStream(dataSourceMapping.getBytes()));
    }

    private Map<Integer, DataSourceConfig> buildDataSourceMap(InputStream is) {
        Map<Integer, DataSourceConfig> map = new HashMap();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            NodeList dataSourceList = doc.getElementsByTagName(DATA_SOURCE);

            for (int i = 0; i < dataSourceList.getLength(); i++) {
                Node dsNode = dataSourceList.item(i);
                if(dsNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element dsElement = (Element)dsNode;
                    String id = dsElement.getAttribute(DATA_SOURCE_ID).trim();
                    Boolean enabled = Boolean.valueOf(dsElement.getAttribute(DATA_SOURCE_ENABLED).trim());

                    NodeList r = dsElement.getElementsByTagName(DATA_SOURCE_SHARDID_RANGE);
                    Element e = (Element)r.item(0);
                    NodeList u = dsElement.getElementsByTagName(DATA_SOURCE_JDBC_URL_TEMPLATE);
                    Element ee = (Element)u.item(0);

                    String range = e.getTextContent().trim();
                    String url = ee.getTextContent().trim();

                    DataSourceConfig config = new DataSourceConfig(id, enabled, url, range);
                    for (int j = config.getRange().getStart(); j <= config.getRange().getEnd(); j++) {
                        map.put(Integer.valueOf(j), config);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("init shard data source mapper failed!", e);
        }

        return Collections.unmodifiableMap(map);
    }

    public DataSourceConfig getDataSourceConfigByShardId(int shardId) {
        DataSourceConfig config = this.dataSourceConfigMap.get(Integer.valueOf(shardId));
        if (config == null) {
            throw new RuntimeException(String.format("Can't find data source config of shard %s", shardId));
        }

        return  config;
    }
}
