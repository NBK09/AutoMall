DO
$$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'ad_photos'
          AND column_name = 'object_key'
    )
       AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'ad_photos'
          AND column_name = 's3_key'
    ) THEN
        ALTER TABLE public.ad_photos RENAME COLUMN object_key TO s3_key;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'ad_photos'
          AND column_name = 'object_key'
    )
       AND EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'ad_photos'
          AND column_name = 's3_key'
    ) THEN
        EXECUTE 'UPDATE public.ad_photos SET s3_key = object_key WHERE (s3_key IS NULL OR btrim(s3_key) = '''') AND object_key IS NOT NULL';
        ALTER TABLE public.ad_photos ALTER COLUMN object_key DROP NOT NULL;
        ALTER TABLE public.ad_photos DROP COLUMN object_key;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'ad_photos'
          AND column_name = 's3_key'
    ) THEN
        ALTER TABLE public.ad_photos ALTER COLUMN s3_key SET NOT NULL;
    END IF;
END
$$;
