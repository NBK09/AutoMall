package com.auto.mall.ad.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdServiceTest {

    @Test
    void extractS3Key_shouldReturnFileNameFromUrlPath() {
        String key = AdService.extractS3Key("/uploads/3d44-photo.jpg");

        assertEquals("3d44-photo.jpg", key);
    }

    @Test
    void extractS3Key_shouldStripQueryStringAndFragment() {
        String key = AdService.extractS3Key("https://cdn.example.com/a/b/car.png?x=1#section");

        assertEquals("car.png", key);
    }

    @Test
    void extractS3Key_shouldFailForBlankOrInvalidTail() {
        assertThrows(IllegalArgumentException.class, () -> AdService.extractS3Key("   "));
        assertThrows(IllegalArgumentException.class, () -> AdService.extractS3Key("https://cdn.example.com/path/"));
    }
}
