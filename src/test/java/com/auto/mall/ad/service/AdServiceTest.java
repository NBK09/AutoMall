package com.auto.mall.ad.service;

import com.auto.mall.ad.entity.Ad;
import com.auto.mall.ad.entity.AdPhoto;
import com.auto.mall.vehicle.brand.entity.Brand;
import com.auto.mall.vehicle.generation.entity.Generation;
import com.auto.mall.vehicle.model.entity.Model;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdServiceTest {

    @Test
    void extractS3Key_shouldReturnFileNameFromUrlPath() {
        assertEquals("3d44-photo.jpg", AdService.extractS3Key("/uploads/3d44-photo.jpg"));
    }

    @Test
    void extractS3Key_shouldStripQueryStringAndFragment() {
        assertEquals("car.png", AdService.extractS3Key("https://cdn.example.com/a/b/car.png?x=1#section"));
    }

    @Test
    void extractS3Key_shouldFailForBlankOrInvalidTail() {
        assertThrows(IllegalArgumentException.class, () -> AdService.extractS3Key("   "));
        assertThrows(IllegalArgumentException.class, () -> AdService.extractS3Key("https://cdn.example.com/path/"));
    }

    @Test
    void validateImmutableIds_shouldFailWhenBrandModelGenerationChanged() {
        Ad ad = new Ad();
        Brand brand = new Brand(); brand.setId(1L);
        Model model = new Model(); model.setId(2L);
        Generation generation = new Generation(); generation.setId(3L);
        ad.setBrand(brand);
        ad.setModel(model);
        ad.setGeneration(generation);

        assertThrows(IllegalArgumentException.class, () -> AdService.validateImmutableIds(ad, 9L, 2L, 3L));
        assertThrows(IllegalArgumentException.class, () -> AdService.validateImmutableIds(ad, 1L, 9L, 3L));
        assertThrows(IllegalArgumentException.class, () -> AdService.validateImmutableIds(ad, 1L, 2L, 9L));
    }

    @Test
    void mergePhotos_shouldSyncDeleteAddOrderAndMain() {
        Ad ad = new Ad();
        AdPhoto keep = AdPhoto.builder().ad(ad).publicUrl("/uploads/keep.jpg").s3Key("keep.jpg").sortOrder(0).isMain(true).build();
        AdPhoto remove = AdPhoto.builder().ad(ad).publicUrl("/uploads/remove.jpg").s3Key("remove.jpg").sortOrder(1).isMain(false).build();

        List<AdPhoto> merged = AdService.mergePhotos(
                ad,
                new ArrayList<>(List.of(keep, remove)),
                List.of("/uploads/keep.jpg", "/uploads/new.jpg"),
                1
        );

        assertEquals(2, merged.size());
        assertEquals("/uploads/keep.jpg", merged.get(0).getPublicUrl());
        assertEquals(0, merged.get(0).getSortOrder());
        assertFalse(merged.get(0).getIsMain());

        assertEquals("/uploads/new.jpg", merged.get(1).getPublicUrl());
        assertEquals("new.jpg", merged.get(1).getS3Key());
        assertTrue(merged.get(1).getIsMain());
    }
}
