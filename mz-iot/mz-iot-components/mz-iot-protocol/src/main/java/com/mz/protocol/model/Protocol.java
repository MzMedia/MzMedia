package com.mz.protocol.model;

import lombok.Data;

@Data
public class Protocol {
    public static final String STATE_STOPPED = "stopped";
    public static final String STATE_RUNNING = "running";
    public static final String CODEC_TYPE_JSON = "JSON";
    public static final String CODEC_TYPE_XML = "XML";
    public static final String CODEC_TYPE_SOAP = "SOAP";
    public static final String CODEC_TYPE_PROTOBUF = "PROTOBUF";
    public static final String CODEC_TYPE_CUSTOM = "CUSTOM";

    private String id;

    private String pid;

    private String name;

    private String jarFile;

    private String network;

    private String config;

    private String codec;

    private String codecType = CODEC_TYPE_CUSTOM;

    private String scriptid;

    private String state;

    private Long createAt;
}
