CREATE TABLE public.chart_metric
(
    date timestamp without time zone NOT NULL,
    indicator character varying(255) COLLATE pg_catalog."default",
    value_1_min double precision,
    value_5_min double precision,
    value_15_min double precision,
    value_30_min double precision,
    value_60_min double precision,
    CONSTRAINT ohlc_pkey PRIMARY KEY (date,indicator)


)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.chart_metric
    OWNER to postgres;


CREATE SEQUENCE public.recalculation_sequence START 1;

CREATE TABLE public.recalculation
(
    id bigint NOT NULL,
    targettime  timestamp without time zone,
    CONSTRAINT recalculation_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.recalculation
    OWNER to postgres;