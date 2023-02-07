ALTER TABLE movie DROP COLUMN watched_on;

create table multimedia.movie_user_details (movie_id bigint not null, user_id bigint not null, watched_on date, primary key (movie_id, user_id));
