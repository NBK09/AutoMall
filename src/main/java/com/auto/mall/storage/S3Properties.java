//package com.auto.mall.storage;
//
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//@Data
//@ConfigurationProperties(prefix = "app.s3")
//public class S3Properties {
//    private String endpoint;
//    private String region;
//    private String bucket;
//    private String accessKey;
//    private String secretKey;
//
//    /** Например: https://automall-prod.s3.ru-7.storage.selcloud.ru */
//    private String publicBaseUrl;
//
//    /** Префикс ключей, например ads */
//    private String photosPrefix = "ads";
//
//    /** TTL presigned URL в секундах (обычно 5-15 минут) */
//    private long presignExpiresSeconds = 900;
//}