CREATE TABLE public.stock
(
    time timestamp without time zone  NOT NULL,
    metaofferid bigint NOT NULL,
    name varchar(255) NOT NULL,
    amount integer NOT NULL,

    CONSTRAINT stock_pkey PRIMARY KEY (time, metaofferid)
);

ALTER INDEX IF EXISTS rss_entries_pkey   RENAME TO price_pkey;