/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.docs;

import com.wordnik.swagger.model.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import scala.collection.immutable.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The reader to load samples.
 */
public class OculusSamplesReader {
    private Logger logger = LoggerFactory.getLogger(OculusSamplesReader.class);
    private DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder dBuilder = getDocumentBuilder();
    private Map<String, List<Sample>> samples = readJarSamples();

    public List<Sample> readSamples(String method, String endpoint) {
        Pattern pattern = Pattern.compile("\\{[^}]+\\}");
        Matcher matcher = pattern.matcher(endpoint);
        endpoint = matcher.replaceAll("{id}");
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }

        List<Sample> result = samples.get(method + " " + endpoint);
        if (result == null) {
            return toScalaList(new ArrayList<Sample>());
        }
        return result;
    }

    private Map<String, List<Sample>> readJarSamples() {
        Map<String, java.util.List<Sample>> results = new HashMap<>();
        try {
            Enumeration<URL> en = this.getClass().getClassLoader().getResources("samples/");
            if (en.hasMoreElements()) {
                URL url = en.nextElement();
                java.util.List<String> paths = getJarFiles(url.getPath());
                for (String path : paths) {
                    try (InputStream inputStream = getInputStream(url, path)) {
                        Document doc = dBuilder.parse(inputStream);
                        parseSamples(results, doc);
                    }
                    catch (SAXException ex) {
                        logger.warn("Failed to parse xml: " + path);
                        throw new RuntimeException(ex);
                    }
                    catch (IOException ex) {
                        logger.warn("Failed to load xml: " + path);
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        catch (IOException ex) {
            logger.warn("Failed to read jar file for samples.");
            throw new RuntimeException(ex);
        }

        Map<String, List<Sample>> finalResults = new HashMap<>();
        for (String key : results.keySet()) {
            finalResults.put(key, toScalaList(results.get(key)));
        }

        return finalResults;
    }

    private InputStream getInputStream(URL url, String path) throws IOException {
        if (url.getPath().contains("!")) {
            return this.getClass().getClassLoader().getResourceAsStream(path);
        } else {
            return new FileInputStream(path);
        }
    }

    private java.util.List<String> getJarFiles(String jarPath) {
        java.util.List<String> resourcePathes = new ArrayList<>();
        String[] jarInfo = jarPath.split("!");
        if (jarInfo.length == 2) {
            String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
            String packagePath = jarInfo[1].substring(1);
            try {
                JarFile jarFile = new JarFile(jarFilePath);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String entryName = jarEntry.getName();
                    if (entryName.startsWith(packagePath) && entryName.endsWith(".xml")) {
                        resourcePathes.add(entryName);
                    }
                }
            } catch (Exception ex) {
                logger.warn("Unable to getJarFiles: " + jarPath);
                throw new RuntimeException(ex);
            }
        } else {
            // check whether this is just a folder
            File folder = new File(jarPath);
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".xml")) {
                    resourcePathes.add(file.getAbsolutePath());
                }
            }
        }

        return resourcePathes;
    }

    private <T> List<T> toScalaList(java.util.List<T> javaList) {
        return scala.collection.immutable.List.fromIterator(
                scala.collection.JavaConversions.asScalaIterator(javaList.iterator()));
    }

    private void parseSamples(Map<String, java.util.List<Sample>> results, Document doc) {
        NodeList nodes = doc.getDocumentElement().getElementsByTagName("apiSamples");
        for (int i = 0; i < nodes.getLength(); ++i) {
            Element element = (Element)nodes.item(i);
            String method = element.getAttribute("method");
            String endpoint = element.getAttribute("endpoint");

            String key = method.toUpperCase() + " " + endpoint;
            if (!results.containsKey(key)) {
                results.put(key, new ArrayList<Sample>());
            }
            java.util.List<Sample> samples = results.get(key);

            parseApiSamples(samples, element);
        }
    }

    private void parseApiSamples(java.util.List<Sample> results, Element apiSampleNode) {
        NodeList sampleNodes = apiSampleNode.getElementsByTagName("sample");
        for (int i = 0; i < sampleNodes.getLength(); ++i) {
            Element element = (Element)sampleNodes.item(i);

            String description = element.getAttribute("description");
            String requestUrl = readChildElement(element, "requestUrl");
            if (requestUrl == null || requestUrl.length() == 0) {
                throw new RuntimeException("Mandated element requestUrl missing.");
            }
            String requestHeaders = readChildElement(element, "requestHeaders");
            String requestBody = readChildElement(element, "requestBody");
            String responseCode = readChildElement(element, "responseCode");
            if (responseCode == null) {
                throw new RuntimeException("Mandated element responseCode missing.");
            }
            String responseHeaders = readChildElement(element, "responseHeaders");
            String responseBody = readChildElement(element, "responseBody");

            results.add(new Sample(
                    description,
                    requestUrl,
                    scala.Option.apply(requestHeaders),
                    scala.Option.apply(requestBody),
                    responseCode,
                    scala.Option.apply(responseHeaders),
                    scala.Option.apply(responseBody)
            ));
        }
    }

    private String readChildElement(Element parent, String tagName) {
        NodeList child = parent.getElementsByTagName(tagName);
        if (child.getLength() > 1) {
            throw new RuntimeException(String.format("Multiple %s found in %s", tagName, parent.getTagName()));
        }
        if (child.getLength() == 0) {
            return null;
        }
        return child.item(0).getTextContent();
    }

    private DocumentBuilder getDocumentBuilder() {
        try {
            return dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
