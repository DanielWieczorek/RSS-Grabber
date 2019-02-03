CREATE TABLE public.tradingevaluationresult
(
    currenttime character varying(255) COLLATE pg_catalog."default" NOT NULL,
    targettime character varying(255) COLLATE pg_catalog."default" NOT NULL,
    predicteddelta double precision,
    CONSTRAINT tradingevaluationresult_pkey PRIMARY KEY (currenttime, targettime)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.tradingevaluationresult
    OWNER to postgres;