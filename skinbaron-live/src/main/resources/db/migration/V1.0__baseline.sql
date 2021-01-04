CREATE TABLE public.price
(
    time timestamp without time zone  NOT NULL,
    metaofferid bigint NOT NULL,
    minimum decimal  NOT NULL ,

    CONSTRAINT rss_entries_pkey PRIMARY KEY (time, metaofferid)
)