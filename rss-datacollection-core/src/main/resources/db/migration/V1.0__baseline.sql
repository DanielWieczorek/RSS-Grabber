CREATE TABLE public.rss_entries
(
    uri character varying(255) COLLATE pg_catalog."default" NOT NULL,
    createdat timestamp without time zone,
    description character varying(1024) COLLATE pg_catalog."default",
    feedurl character varying(255) COLLATE pg_catalog."default",
    heading character varying(255) COLLATE pg_catalog."default",
    publicationdate timestamp without time zone,
    CONSTRAINT rss_entries_pkey PRIMARY KEY (uri)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.rss_entries
    OWNER to postgres;