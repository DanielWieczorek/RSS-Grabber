CREATE TABLE public.sentimentattime
(
    "time" timestamp without time zone NOT NULL,
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

CREATE SEQUENCE recalculation_sequence START 1;

CREATE TABLE public.recalculation
(
    id bigint NOT NULL,
    targettime character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT recalculation_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.recalculation
    OWNER to postgres;