-- Owner password: TELVAgerlAcHerSu
INSERT INTO users(id, email, password, role)
VALUES (random_uuid(), 'owner@email.com', '$2a$10$lDKX1.bX8VBU83rCeHhZKu5efdurZhlfnideRaWiZLBIUclVM.3/m', 'OWNER');

INSERT INTO users(id, email, password, role)
VALUES (random_uuid(), 'admin@email.com', '$2a$10$lDKX1.bX8VBU83rCeHhZKu5efdurZhlfnideRaWiZLBIUclVM.3/m', 'ADMIN');

INSERT INTO users(id, email, password, role)
VALUES (random_uuid(), 'user@email.com', '$2a$10$lDKX1.bX8VBU83rCeHhZKu5efdurZhlfnideRaWiZLBIUclVM.3/m', 'USER');