CREATE TABLE public.account
(
    time timestamp without time zone,
    btc decimal,
    eur  decimal,
    eur_equivalent  decimal,
    CONSTRAINT account_pkey PRIMARY KEY (time)
)