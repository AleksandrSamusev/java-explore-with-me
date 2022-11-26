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

insert into events (annotation, category_id, description, event_date,
                    initiator_id, paid, participant_limit, created_on,
                    published_on, request_moderation, confirmed_requests,
                    state, title, available, lat, lon, views, rating)
values ('выставка породистых котиков и еще много всего интересного',
        1, 'У нас есть чем удивить каждого!' ||
           ' Веселая анимация и конкурсы с призами и подарками,' ||
           ' шоу-показ кошачьей моды, детская площадка с аквагримом' ||
           ' и «изо-студией» - скучно не будет никому!',
        '2022-10-10 10:00:00.0000000', 1, false, 0, '2022-09-10 10:00:00.0000000', '2022-09-11 10:00:00.0000000',
        false, 2, 'PUBLISHED', 'Большая кошачья выставка', true, 23.33, 11.98, 1, 0);

insert into requests (status, event_id, requester_id, created)
values ('CONFIRMED', 1, 2, '2022-09-12 10:00:00.0000000');

insert into requests (status, event_id, requester_id, created)
values ('CONFIRMED', 1, 3, '2022-09-12 10:00:00.0012000');