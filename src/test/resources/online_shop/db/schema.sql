CREATE TABLE users (
	id SERIAL PRIMARY KEY,
	login VARCHAR(50) UNIQUE NOT NULL,
	password VARCHAR(200) NOT NULL,
	role VARCHAR(50) NOT NULL,
	salt VARCHAR(200) NOT NULL
)

CREATE TABLE products (
   id SERIAL PRIMARY KEY,
   productName VARCHAR(200) NOT NULL,
   price FLOAT (2) NOT NULL,
   description VARCHAR(500) NOT NULL,
   rating INTEGER,
   pathToImage VARCHAR(200) NOT NULL,
   creationDate timestamp NOT NULL
);

ololo