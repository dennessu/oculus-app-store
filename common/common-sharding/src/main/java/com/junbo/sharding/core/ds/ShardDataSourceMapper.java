/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core.ds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by minhao on 3/8/14.
 */
public class ShardDataSourceMapper {
    private static final String DATA_SOURCE = "dataSource";
    private static final String DATA_SOURCE_ID = "id";
    private static final String DATA_SOURCE_ENABLED = "enabled";
    private static final String DATA_SOURCE_SHARDID_RANGE = "shardIdRange";
    private static final String DATA_SOURCE_JDBC_URL_TEMPLATE = "jdbcUrlTemplate";

    private Set<DataSourceConfig> dataSourceConfigSet;

    public ShardDataSourceMapper(String dataSourceMapping) {
        this.dataSourceConfigSet = buildDataSourceSet(new ByteArrayInputStream(dataSourceMapping.getBytes()));
    }

    private Set<DataSourceConfig> buildDataSourceSet(InputStream is) {
        Set<DataSourceConfig> set = new HashSet();
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

                    set.add(new DataSourceConfig(id, enabled, url, range));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("init shard datasource mapper failed!", e);
        }

        return Collections.unmodifiableSet(set);
    }

    public DataSourceConfig getDataSourceConfigByShardId(int shardId) {
        for(Iterator<DataSourceConfig> it = this.dataSourceConfigSet.iterator(); it.hasNext();) {
            DataSourceConfig c = it.next();
            if (c.contains(shardId)) {
                return c;
            }
        }

        throw new RuntimeException(String.format("Can't find data source config of shard %s", shardId));
    }
}
