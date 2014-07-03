/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * System requirement.
 */
public class SystemRequirement {
    @ApiModelProperty(position = 1, required = true, value = "Operating system")
    private String operatingSystem;
    @ApiModelProperty(position = 2, required = true, value = "Processor")
    private String processor;
    @ApiModelProperty(position = 3, required = true, value = "Memory")
    private String memory;
    @ApiModelProperty(position = 4, required = true, value = "Graphics")
    private String graphics;
    @ApiModelProperty(position = 5, required = true, value = "DirectX")
    private String directX;
    @ApiModelProperty(position = 6, required = true, value = "Hard drive")
    private String hardDrive;
    @ApiModelProperty(position = 7, required = true, value = "Sound card")
    private String soundCard;
    @ApiModelProperty(position = 8, required = true, value = "Multiplayer")
    private String multiplayer;
    @ApiModelProperty(position = 9, required = true, value = "Other")
    private String other;
    @ApiModelProperty(position = 10, required = true, value = "Notice")
    private String notice;
    @ApiModelProperty(position = 11, required = true, value = "Notice important")
    private String noticeImportant;

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getGraphics() {
        return graphics;
    }

    public void setGraphics(String graphics) {
        this.graphics = graphics;
    }

    public String getDirectX() {
        return directX;
    }

    public void setDirectX(String directX) {
        this.directX = directX;
    }

    public String getHardDrive() {
        return hardDrive;
    }

    public void setHardDrive(String hardDrive) {
        this.hardDrive = hardDrive;
    }

    public String getSoundCard() {
        return soundCard;
    }

    public void setSoundCard(String soundCard) {
        this.soundCard = soundCard;
    }

    public String getMultiplayer() {
        return multiplayer;
    }

    public void setMultiplayer(String multiplayer) {
        this.multiplayer = multiplayer;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getNoticeImportant() {
        return noticeImportant;
    }

    public void setNoticeImportant(String noticeImportant) {
        this.noticeImportant = noticeImportant;
    }
}
