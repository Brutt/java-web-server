INSERT INTO users (login, password, role, salt) VALUES ('admin', '96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e', 'ADMIN', '123');
INSERT INTO users (login, password, role, salt) VALUES ('user', '96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e', 'USER', '123');

INSERT INTO products (productName,  price,  description, rating, pathToImage,  creationDate) values ('Java1.8', 120.95, 'Java version 1.8', 5, '/home/java18.jpg', now());
INSERT INTO products (productName,  price,  description, rating, pathToImage,  creationDate) values ('Java10', 180.95, 'Java version 10', 4, '/home/java10.jpg', now());