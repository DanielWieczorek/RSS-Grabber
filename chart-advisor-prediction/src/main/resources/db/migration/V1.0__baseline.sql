CREATE TABLE public.tradingevaluationresult
(
    currenttime timestamp without time zone NOT NULL,
    targettime timestamp without time zone NOT NULL,
    predicteddelta double precision,
    CONSTRAINT tradingevaluationresult_pkey PRIMARY KEY (currenttime, targettime)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.tradingevaluationresult
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