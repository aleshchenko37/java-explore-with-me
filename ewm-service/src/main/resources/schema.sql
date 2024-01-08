create TABLE IF NOT EXISTS users (
  users_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  users_email VARCHAR(256) NOT NULL,
  users_name VARCHAR(256) NOT NULL,
  CONSTRAINT pk_users PRIMARY KEY (users_id),
  CONSTRAINT uq_user_email UNIQUE (users_email)
);

create TABLE IF NOT EXISTS categories (
  categories_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  categories_name VARCHAR(64) NOT NULL,
  CONSTRAINT pk_categories PRIMARY KEY (categories_id),
  CONSTRAINT uq_category_name UNIQUE (categories_name)
);

create TABLE IF NOT EXISTS locations (
  locations_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  locations_lat REAL NOT NULL,
  locations_lon REAL NOT NULL,
  locations_radius REAL NOT NULL,
  CONSTRAINT pk_locations PRIMARY KEY (locations_id)
);

create TABLE IF NOT EXISTS events (
  events_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  events_annotation VARCHAR(2048) NOT NULL,
  events_category_id BIGINT NOT NULL,
  events_confirmed_requests BIGINT,
  events_created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  events_description VARCHAR(8192) NOT NULL,
  events_event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  events_initiator_id BIGINT NOT NULL,
  events_location_id BIGINT NOT NULL,
  events_paid BOOLEAN NOT NULL,
  events_participant_limit INTEGER NOT NULL,
  events_published_on TIMESTAMP WITHOUT TIME ZONE,
  events_request_moderation BOOLEAN,
  events_state VARCHAR(16) NOT NULL,
  events_title VARCHAR(128) NOT NULL,
  CONSTRAINT pk_events PRIMARY KEY (events_id),
  CONSTRAINT fk_event_to_categories FOREIGN KEY(events_category_id)
    REFERENCES categories(categories_id),
  CONSTRAINT fk_event_to_users FOREIGN KEY(events_initiator_id)
    REFERENCES users(users_id) ON update RESTRICT ON delete CASCADE,
  CONSTRAINT fk_event_to_locations FOREIGN KEY(events_location_id)
    REFERENCES locations(locations_id)
);

create TABLE IF NOT EXISTS participation_requests (
  participation_requests_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  participation_requests_created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  participation_requests_event_id BIGINT NOT NULL,
  participation_requests_requester_id BIGINT NOT NULL,
  participation_requests_status VARCHAR(16) NOT NULL,
  CONSTRAINT pk_participation_requests PRIMARY KEY (participation_requests_id),
  CONSTRAINT fk_participation_request_to_events FOREIGN KEY(participation_requests_event_id)
    REFERENCES events(events_id) ON update RESTRICT ON delete CASCADE,
  CONSTRAINT fk_participation_request_to_users FOREIGN KEY(participation_requests_requester_id)
    REFERENCES users(users_id) ON update RESTRICT ON delete CASCADE
);

CREATE TABLE IF NOT EXISTS compilations (
    compilations_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    compilations_title VARCHAR(64) NOT NULL,
    compilations_pinned BOOLEAN NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (compilations_id)
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilations (compilations_id),
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (events_id)
);

CREATE FUNCTION distance(lat1 float, lon1 float, lat2 float, lon2 float)
    RETURNS float
AS
'
    declare
        dist      float = 0;
        rad_lat1  float;
        rad_lat2  float;
        theta     float;
        rad_theta float;
    BEGIN
        IF lat1 = lat2 AND lon1 = lon2
        THEN
            RETURN dist;
        ELSE
            -- переводим градусы широты в радианы
            rad_lat1 = pi() * lat1 / 180;
            -- переводим градусы долготы в радианы
            rad_lat2 = pi() * lat2 / 180;
            -- находим разность долгот
            theta = lon1 - lon2;
            -- переводим градусы в радианы
            rad_theta = pi() * ((lon1 - lon2) / 180);
            -- находим длину ортодромии
            dist = sin(pi() * lat1 / 180) * sin(pi() * lat2 / 180) +
                   cos(pi() * lat1 / 180) * cos(pi() * lat2 / 180) * cos(pi() * ((lon1 - lon2) / 180));

            IF dist > 1
            THEN
                dist = 1;
            END IF;

            dist = acos(sin(pi() * lat1 / 180) * sin(pi() * lat2 / 180) +
                        cos(pi() * lat1 / 180) * cos(pi() * lat2 / 180) * cos(pi() * ((lon1 - lon2) / 180)));
            -- переводим радианы в градусы
            dist = dist * 180 / pi();
            -- переводим градусы в километры
            dist = dist * 60 * 1.8524;

            RETURN dist;
        END IF;
    END;
'
    LANGUAGE PLPGSQL;