/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.cloudant.json.annotations.CloudantProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Supported locale.
 */
public class SupportedLocale {
    @ApiModelProperty(position = 1, required = true, value = "Audio")
    private boolean audio;
    @JsonProperty("interface")
    @CloudantProperty("interface")
    @ApiModelProperty(position = 2, required = true, value = "Interface")
    private boolean interfac3;
    @ApiModelProperty(position = 3, required = true, value = "Subtitles")
    private boolean subtitles;

    public boolean isAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public boolean isInterfac3() {
        return interfac3;
    }

    public void setInterfac3(boolean interfac3) {
        this.interfac3 = interfac3;
    }

    public boolean isSubtitles() {
        return subtitles;
    }

    public void setSubtitles(boolean subtitles) {
        this.subtitles = subtitles;
    }
}
