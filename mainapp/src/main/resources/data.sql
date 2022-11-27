insert into categories (name)
values ('выставки');
insert into categories (name)
values ('кинопремьеры');
insert into categories (name)
values ('музыкальные концерты');

insert into users (name, email, rating)
values ('user1', 'user1@post.com', 0);
insert into users (name, email, rating)
values ('user2', 'user2@post.com', 0);
insert into users (name, email, rating)
values ('user3', 'user3@post.com', 0);
insert into users (name, email, rating)
values ('user4', 'user4@post.com', 0);
insert into users (name, email, rating)
values ('user5', 'user5@post.com', 0);

insert into events (annotation, category_id, description, event_date,
                    initiator_id, paid, participant_limit, created_on,
                    published_on, request_moderation, confirmed_requests,
                    state, title, available, lat, lon, views, rating, rating_flag)
values ('выставка породистых котиков и еще много всего интересного',
        1, 'У нас есть чем удивить каждого!' ||
           ' Веселая анимация и конкурсы с призами и подарками,' ||
           ' шоу-показ кошачьей моды, детская площадка с аквагримом' ||
           ' и «изо-студией» - скучно не будет никому!',
        '2024-10-10 10:00:00.0000000', 1, false, 0, '2022-09-10 10:00:00.0000000', '2022-09-11 10:00:00.0000000',
        false, 0, 'PUBLISHED', 'Большая кошачья выставка', true, 23.33, 11.98, 0, 0, true);

insert into events (annotation, category_id, description, event_date,
                    initiator_id, paid, participant_limit, created_on,
                    published_on, request_moderation, confirmed_requests,
                    state, title, available, lat, lon, views, rating, rating_flag)
values ('премьера нового фильма Кристофера Нолана "Интерстеллар"',
        2, 'Когда засуха, пыльные бури и вымирание растений приводят человечество к продовольственному кризису,' ||
           ' коллектив исследователей и учёных отправляется сквозь червоточину (которая предположительно' ||
           ' соединяет области пространства-времени через большое расстояние) в путешествие,' ||
           ' чтобы превзойти прежние ограничения для космических путешествий человека и найти планету' ||
           ' с подходящими для человечества условиями.',
        '2022-10-10 10:00:00.0000000', 1, true, 2, '2022-09-10 10:00:00.0000000', '2022-09-11 10:00:00.0000000',
        false, 0, 'PUBLISHED', 'премьера фильма "Интерстеллар"', true, 67.12, 76.76, 0, 0, true);

insert into requests (status, event_id, requester_id, created)
values ('PENDING', 2, 2, '2022-09-12 10:00:00.0000000');

insert into requests (status, event_id, requester_id, created)
values ('PENDING', 2, 3, '2022-09-12 10:00:00.0012000');