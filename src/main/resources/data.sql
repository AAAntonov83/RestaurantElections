INSERT INTO users (email, name, password)
VALUES ('user@gmail.com', 'User', '{noop}password'),
       ('admin@gmail.com', 'Admin', '{noop}admin'),
       ('guest@gmail.com', 'Guest', '{noop}guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2),
       ('USER', 3);

INSERT INTO meal (name, price)
VALUES ('MEAL_1', 10),
       ('MEAL_2', 15),
       ('MEAL_3', 20);

INSERT INTO restaurant (name)
VALUES ('RESTAURANT_1'),
       ('RESTAURANT_2'),
       ('RESTAURANT_WITHOUT_MENU');

INSERT INTO menu (restaurant_id, date)
VALUES (1, DATEADD(DAY , -1, DATE_TRUNC(DAY, now()))),
       (2, DATEADD(DAY , -1, DATE_TRUNC(DAY, now()))),
       (1, DATE_TRUNC(DAY, now())),
       (2, DATE_TRUNC(DAY, now()));

INSERT INTO menu_meals (menu_id, meal_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (3, 1),
       (3, 2),
       (4, 1);

INSERT INTO vote (restaurant_id, user_id, date_time)
VALUES (1, 1, DATEADD(HOUR, 9, DATEADD(DAY , -1, DATE_TRUNC(DAY, now())))),
       (2, 2, DATEADD(HOUR, 8, DATEADD(DAY , -1, DATE_TRUNC(DAY, now())))),
       (1, 2, DATEADD(HOUR, 11, DATE_TRUNC(DAY, now()))),
       (2, 1, DATEADD(HOUR, 10, DATE_TRUNC(DAY, now())));