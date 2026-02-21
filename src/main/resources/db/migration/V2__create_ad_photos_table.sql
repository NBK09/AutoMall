CREATE TABLE ad_photos (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL,
    s3_key VARCHAR(1024) NOT NULL,
    public_url TEXT,
    sort_order INT NOT NULL DEFAULT 0,
    is_main BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_ad_photos_ad_id
        FOREIGN KEY (ad_id)
            REFERENCES ads (id)
            ON DELETE CASCADE,
    CONSTRAINT uk_ad_photos_s3_key UNIQUE (s3_key)
);

CREATE INDEX idx_ad_photos_ad_id ON ad_photos (ad_id);
CREATE INDEX idx_ad_photos_sort_order ON ad_photos (sort_order);
