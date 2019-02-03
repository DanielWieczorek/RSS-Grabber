CREATE TABLE public.credentials
(
    username character varying(255) COLLATE pg_catalog."default" NOT NULL,
    iterations integer,
    passwordhash character varying(255) COLLATE pg_catalog."default",
    salt character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT credentials_pkey PRIMARY KEY (username)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.credentials
    OWNER to postgres;


CREATE TABLE public.session
(
    username character varying(255) COLLATE pg_catalog."default" NOT NULL,
    expirationdate character varying(255) COLLATE pg_catalog."default",
    token character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT session_pkey PRIMARY KEY (username)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.session
    OWNER to postgres;
