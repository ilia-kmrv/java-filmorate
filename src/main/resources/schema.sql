drop table	MPA_RATINGS IF EXISTS CASCADE;
drop table	MPA_RATING IF EXISTS CASCADE;
drop table	FILMS IF EXISTS	CASCADE	;
drop table	GENRES IF	EXISTS CASCADE	;
drop table	FILM_GENRE IF EXISTS CASCADE	;
drop table	USERS IF	EXISTS CASCADE	;
drop table	FRIENDSHIP IF EXISTS CASCADE	;
drop table LIKES IF EXISTS CASCADE	;

create TABLE IF NOT EXISTS mpa_ratings (
		id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
		name VARCHAR(5)
);

create TABLE IF NOT EXISTS films (
        id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name VARCHAR NOT NULL,
        description VARCHAR(200),
        release_date DATE NOT NULL,
        duration INTEGER,
        mpa_rating INTEGER REFERENCES mpa_ratings (id)
);

create TABLE IF NOT EXISTS genres (
		id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
		name VARCHAR
);

create TABLE IF NOT EXISTS	film_genre (
		film_id BIGINT REFERENCES films (id) ON delete CASCADE,
		genre_id INTEGER REFERENCES genres (id) ON delete CASCADE
);

create TABLE IF NOT EXISTS	users (
		id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
		email VARCHAR NOT NULL,
		login VARCHAR NOT NULL,
		name VARCHAR,
		birthday DATE NOT NULL
);

create TABLE IF NOT EXISTS	friendship (
		first_user_id BIGINT REFERENCES users (id) ON delete CASCADE,
		second_user_id BIGINT REFERENCES users (id) ON delete CASCADE,
		status BOOLEAN DEFAULT FALSE
);

create TABLE IF NOT EXISTS	likes (
	film_id BIGINT REFERENCES films (id),
	user_id BIGINT REFERENCES users (id)
);



