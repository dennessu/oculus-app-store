/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

/**
 * The Image class.
 */
public class Image {

    /**
     * The image type.
     */
    public static enum  ImageType {

    }

    /**
     * The dimension class.
     */
    public static class Dimension {
        private Integer width;
        private Integer height;

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }
    }

    private String fillColorRgb;
    private String imageUrl;
    private Dimension dimension;
    private ImageType imageType;

    public String getFillColorRgb() {
        return fillColorRgb;
    }

    public void setFillColorRgb(String fillColorRgb) {
        this.fillColorRgb = fillColorRgb;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }
}
