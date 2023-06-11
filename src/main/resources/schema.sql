DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
	user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	user_name VARCHAR (255) NOT NULL,
	email VARCHAR (255) UNIQUE NOT NULL
);

CREATE TABLE requests (
	request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	description VARCHAR (255) NOT NULL,
	creation_date TIMESTAMP NOT NULL,
	author_id BIGINT REFERENCES users (user_id) NOT NULL
);

CREATE TABLE items (
	item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	item_name VARCHAR (255) NOT NULL,
	description VARCHAR (255),
	available BOOL DEFAULT true NOT NULL,
	owner_id BIGINT REFERENCES users (user_id) NOT NULL,
	request_id BIGINT REFERENCES requests (request_id)
);

CREATE TABLE bookings (
	booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	start_time TIMESTAMP NOT NULL,
	end_time TIMESTAMP NOT NULL,
	item_id BIGINT REFERENCES items (item_id) NOT NULL,
	booker_id BIGINT REFERENCES users (user_id) NOT NULL,
	status INTEGER NOT NULL
);

CREATE TABLE comments (
	comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	text VARCHAR (255) NOT NULL,
	item_id BIGINT REFERENCES items (item_id) NOT NULL,
	author_id BIGINT REFERENCES users (user_id) NOT NULL,
	creation_date TIMESTAMP NOT NULL
);