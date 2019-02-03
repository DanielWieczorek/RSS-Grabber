CREATE TABLE public.sentimentattime
(
    "time" character varying(255) COLLATE pg_catalog."default" NOT NULL,
    negativeprobability double precision,
    positiveprobability double precision,
    CONSTRAINT sentimentattime_pkey PRIMARY KEY ("time")
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.sentimentattime
    OWNER to postgres;