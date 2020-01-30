CREATE TABLE public.performed_trade
(
    id character varying(255),
    time timestamp without time zone,
    type character varying(1024),
    pair character varying(255),
    amount decimal,
    price  decimal,
    status character varying(255),
    CONSTRAINT trade_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.performed_trade
    OWNER to postgres;