ALTER TABLE movie DROP COLUMN watched_on;
ALTER TABLE book DROP COLUMN read_on;

create table movie_user_details (movie_id bigint not null, user_id bigint not null, watched_on date, primary key (movie_id, user_id));
create table book_user_details (book_id bigint not null, user_id bigint not null, read_on date, primary key (book_id, user_id));