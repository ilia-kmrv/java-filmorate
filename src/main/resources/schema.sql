DROP TABLE	MPA_RATINGS IF EXISTS CASCADE;
DROP TABLE	MPA_RATING IF EXISTS CASCADE;
DROP TABLE	FILMS IF EXISTS	CASCADE	;
DROP TABLE	GENRES IF	EXISTS CASCADE	;
DROP TABLE	FILM_GENRE IF EXISTS CASCADE	;
DROP TABLE	USERS IF	EXISTS CASCADE	;
DROP TABLE	FRIENDSHIP IF EXISTS CASCADE	;
DROP TABLE LIKES IF EXISTS CASCADE	;

CREATE TABLE IF NOT EXISTS mpa_ratings (
		id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
		name VARCHAR(5)
);

CREATE TABLE IF NOT EXISTS films (
        id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name VARCHAR NOT NULL,
        description VARCHAR(200),
        release_date DATE NOT NULL,
        duration INTEGER,
        mpa_rating INTEGER REFERENCES mpa_ratings (id)
);

CREATE TABLE IF NOT EXISTS genres (
		id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
		name VARCHAR
);

CREATE TABLE IF NOT EXISTS	film_genre (
		film_id BIGINT REFERENCES films (id),
		genre_id INTEGER REFERENCES genres (id)
);

CREATE TABLE IF NOT EXISTS	users (
		id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
		email VARCHAR NOT NULL,
		login VARCHAR NOT NULL,
		name VARCHAR,
		birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS	friendship (
		first_user_id BIGINT REFERENCES users (id),
		second_user_id BIGINT REFERENCES users (id),
		status BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS	likes (
	film_id BIGINT REFERENCES films (id),
	user_id BIGINT REFERENCES users (id)
);



