INSERT INTO users (email, name, password)
VALUES ('user@gmail.com', 'User', '{noop}password'),
       ('admin@gmail.com', 'Admin', '{noop}admin'),
       ('guest@gmail.com', 'Guest', '{noop}guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2),
       ('USER', 3);

INSERT INTO dish (restaurant_id, name, price)
VALUES (1, 'DISH_1', 10),
       (1, 'DISH_2', 15),
       (2, 'DISH_3', 20),
       (2, 'DISH_4', 20),
       (1, 'DISH_5_NOT_IN_MENU', 25);

INSERT INTO restaurant (name)
VALUES ('RESTAURANT_1'),
       ('RESTAURANT_2'),
       ('RESTAURANT_WITHOUT_MENU');

INSERT INTO menu (restaurant_id, on_date)
VALUES (1, DATEADD(DAY , -1, now())),
       (2, DATEADD(DAY , -1, now())),
       (1, DATE_TRUNC(DAY, now())),
       (2, DATE_TRUNC(DAY, now()));

INSERT INTO menu_dishes (menu_id, dish_id)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (3, 1),
       (3, 2),
       (4, 4);

INSERT INTO vote (restaurant_id, user_id, date_time)
VALUES (1, 1, DATEADD(HOUR, 9, DATEADD(DAY , -1, DATE_TRUNC(DAY, now())))),
       (2, 2, DATEADD(HOUR, 8, DATEADD(DAY , -1, DATE_TRUNC(DAY, now())))),
       (1, 2, DATEADD(HOUR, 11, DATE_TRUNC(DAY, now()))),
       (2, 1, DATEADD(HOUR, 10, DATE_TRUNC(DAY, now())));