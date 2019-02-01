CREATE FUNCTION public.id_for_case() RETURNS integer
    LANGUAGE plpgsql
    AS $$
declare
  year numeric := extract(year from current_date);
  seq text := 'seq_' || year;
begin
  execute 'create sequence if not exists ' || seq;
  return (year || lpad(nextval(seq)::text, 4, '0'))::integer;
end;
$$;

CREATE FUNCTION public.id_for_medium() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
  mediaID numeric;
begin
  if tg_when = 'AFTER' and tg_op = 'DELETE' then
    update medium set "mediaID" = "mediaID" - 1 where "caseID" = old."caseID" and "mediaID" > old."mediaID";
    return null;
  end if;
  if tg_op = 'UPDATE' then
    if old."caseID" = new."caseID" then
      return new;
    end if;
  end if;
  mediaID := (select max("mediaID")::integer from medium group by "caseID" having "caseID" = new."caseID");
  new."mediaID" := coalesce(mediaID, 0) + 1;
  return new;
end;
$$;

CREATE TABLE public.breed (
    "breed" character varying(20) NOT NULL
);

CREATE TABLE public."case" (
    "caseID" integer DEFAULT public.id_for_case() NOT NULL,
    "timestamp" timestamp without time zone NOT NULL,
    "priority" integer NOT NULL,
    "rescuer" character varying(20),
    "additionalInfo" text,
    "phone" character varying(20) NOT NULL,
    "latitude" real NOT NULL,
    "longitude" real NOT NULL,
    "wasFoundDead" boolean,
    "isClosed" boolean NOT NULL,
    "reporter" character varying(20),
    "wasNotFound" boolean,
    "breed" character varying(20),
    "lastEdited" timestamp without time zone
);

CREATE TABLE public.feed (
    "feedID" integer NOT NULL,
    "author" character varying(20),
    "title" character varying(50) NOT NULL,
    "text" text,
    "timestamp" timestamp without time zone NOT NULL,
    "eventStart" timestamp without time zone,
    "eventEnd" timestamp without time zone
);

CREATE SEQUENCE public.feed_feedid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.feed_feedid_seq OWNED BY public.feed."feedID";

CREATE TABLE public.injury (
    "caseID" integer NOT NULL,
    "footOrLeg" boolean NOT NULL,
    "wing" boolean NOT NULL,
    "headOrEye" boolean NOT NULL,
    "openWound" boolean NOT NULL,
    "paralyzedOrFlightless" boolean NOT NULL,
    "fledgling" boolean NOT NULL,
    "other" boolean NOT NULL,
    "strappedFeet" boolean NOT NULL
);

CREATE TABLE public.medium (
    "caseID" integer NOT NULL,
    "uri" character varying(255) NOT NULL,
    "mediaID" integer NOT NULL,
    "mimeType" character varying(255),
    "thumbnail" character varying(255)
);

CREATE TABLE public."populationMarker" (
    "populationMarkerID" integer NOT NULL,
    "latitude" real NOT NULL,
    "longitude" real NOT NULL,
    "radius" real NOT NULL,
    "description" character varying(60),
    "lastUpdate" timestamp without time zone NOT NULL
);

CREATE TABLE public."populationValue" (
    "pigeonCount" integer NOT NULL,
    "populationMarkerID" integer NOT NULL,
    "timestamp" timestamp without time zone NOT NULL
);

CREATE SEQUENCE public.populationmarker_populationmarkerid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.populationmarker_populationmarkerid_seq OWNED BY public."populationMarker"."populationMarkerID";

CREATE TABLE public.token (
    "tokenID" character varying(32) NOT NULL,
    "username" character varying(20) NOT NULL
);

CREATE TABLE public."user" (
    "username" character varying(20) NOT NULL,
    "phone" character varying(30) NOT NULL,
    "isAdmin" boolean NOT NULL,
    "password" character varying(255) NOT NULL,
    "isActivated" boolean DEFAULT false NOT NULL,
    "registrationToken" character varying(255)
);

ALTER TABLE ONLY public.feed ALTER COLUMN "feedID" SET DEFAULT nextval('public.feed_feedid_seq'::regclass);

ALTER TABLE ONLY public."populationMarker" ALTER COLUMN "populationMarkerID" SET DEFAULT nextval('public.populationmarker_populationmarkerid_seq'::regclass);

ALTER TABLE ONLY public.breed
    ADD CONSTRAINT breed_pkey PRIMARY KEY (breed);

ALTER TABLE ONLY public."case"
    ADD CONSTRAINT case_pkey PRIMARY KEY ("caseID");

ALTER TABLE ONLY public.feed
    ADD CONSTRAINT feed_pk PRIMARY KEY ("feedID");

ALTER TABLE ONLY public.injury
    ADD CONSTRAINT injury_pkey PRIMARY KEY ("caseID");

ALTER TABLE ONLY public.medium
    ADD CONSTRAINT medium_pk PRIMARY KEY (uri);

ALTER TABLE ONLY public."populationMarker"
    ADD CONSTRAINT populationmarker_pkey PRIMARY KEY ("populationMarkerID");

ALTER TABLE ONLY public."populationValue"
    ADD CONSTRAINT populationvalue_pk PRIMARY KEY ("timestamp", "populationMarkerID");

ALTER TABLE ONLY public.token
    ADD CONSTRAINT token_pkey PRIMARY KEY ("tokenID");

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (username);

CREATE TRIGGER id_for_medium_after_trigger AFTER DELETE ON public.medium FOR EACH ROW EXECUTE PROCEDURE public.id_for_medium();

CREATE TRIGGER id_for_medium_before_trigger BEFORE INSERT OR UPDATE ON public.medium FOR EACH ROW EXECUTE PROCEDURE public.id_for_medium();

ALTER TABLE ONLY public."case"
    ADD CONSTRAINT breed_fk FOREIGN KEY (breed) REFERENCES public.breed(breed);

ALTER TABLE ONLY public.feed
    ADD CONSTRAINT feed_user_username_fk FOREIGN KEY (author) REFERENCES public."user"(username);

ALTER TABLE ONLY public.injury
    ADD CONSTRAINT injury_fk FOREIGN KEY ("caseID") REFERENCES public."case"("caseID");

ALTER TABLE ONLY public.medium
    ADD CONSTRAINT medium_fk FOREIGN KEY ("caseID") REFERENCES public."case"("caseID");

ALTER TABLE ONLY public."populationValue"
    ADD CONSTRAINT populationvalue_fk FOREIGN KEY ("populationMarkerID") REFERENCES public."populationMarker"("populationMarkerID");

ALTER TABLE ONLY public."case"
    ADD CONSTRAINT reporter_fk FOREIGN KEY (reporter) REFERENCES public."user"(username);

ALTER TABLE ONLY public."case"
    ADD CONSTRAINT rescuer_fk FOREIGN KEY (rescuer) REFERENCES public."user"(username);

ALTER TABLE ONLY public.token
    ADD CONSTRAINT user_fk FOREIGN KEY (username) REFERENCES public."user"(username);