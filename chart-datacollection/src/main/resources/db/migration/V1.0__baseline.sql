CREATE TABLE public.ohlc
(
    date character varying(255) COLLATE pg_catalog."default" NOT NULL,
    close double precision,
    high double precision,
    low double precision,
    open double precision,
    transactions double precision,
    volume double precision,
    volumeweightedaverage double precision,
    CONSTRAINT ohlc_pkey PRIMARY KEY (date)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.ohlc
    OWNER to postgres;