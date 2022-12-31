create table actor (id bigint generated by default as identity, birth_date date, death_date date, filmweb_url varchar(255), imdb_id varchar(255), name varchar(255), primary key (id));
create table actor_directed_movies (actor_id bigint not null, directed_movies_id bigint not null);
create table actor_played_in_movies (actor_id bigint not null, played_in_movies_id bigint not null);
create table actor_wrote_movies (actor_id bigint not null, wrote_movies_id bigint not null);
create table actor_content_list (id bigint generated by default as identity, name varchar(255), primary key (id));
create table actor_content_list_actor_list (actor_content_list_id bigint not null, actor_list_id bigint not null);
create table movie (id bigint generated by default as identity, filmweb_url varchar(255), im_db_rating real not null, im_db_rating_votes integer, imdb_id varchar(255), plot_local varchar(255), polish_title varchar(255), release_date date, runtime_mins integer, title varchar(255), primary key (id));
create table movie_actor_list (movie_id bigint not null, actor_list_id bigint not null);
create table movie_country_list (movie_id bigint not null, country_list varchar(255));
create table movie_director_list (movie_id bigint not null, director_list_id bigint not null);
create table movie_genre_list (movie_id bigint not null, genre_list varchar(255));
create table movie_writer_list (movie_id bigint not null, writer_list_id bigint not null);
create table movie_content_list (id bigint generated by default as identity, name varchar(255), primary key (id));
create table movie_content_list_movies (movie_content_list_id bigint not null, movies_id bigint not null);
create table users (id bigint generated by default as identity, enabled boolean not null, password varchar(255), user_role integer, username varchar(255), primary key (id));
create table users_actor_list (user_id bigint not null, actor_list_id bigint not null);
create table users_movie_lists (user_id bigint not null, movie_lists_id bigint not null);
alter table users_actor_list add constraint UK_bibvl9epi0di38wbntqhj5ger unique (actor_list_id);
alter table users_movie_lists add constraint UK_nl5w0yq0b1bqcuvxq3ew9xiqw unique (movie_lists_id);
alter table actor_directed_movies add constraint FKi4b0k4ld17p8uwni708u1qb96 foreign key (directed_movies_id) references movie;
alter table actor_directed_movies add constraint FKgid0wdb3e2h7p4rlflqgjq7fl foreign key (actor_id) references actor;
alter table actor_played_in_movies add constraint FKob46mu2dbekg1cxib7oeqr4uy foreign key (played_in_movies_id) references movie;
alter table actor_played_in_movies add constraint FKfmfssusiycn2mf43pd3wtq75h foreign key (actor_id) references actor;
alter table actor_wrote_movies add constraint FKnhu6l6tgnntitffpp34a2uhyn foreign key (wrote_movies_id) references movie;
alter table actor_wrote_movies add constraint FK6v2mbi1om50328qof1oa0bpud foreign key (actor_id) references actor;
alter table actor_content_list_actor_list add constraint FKo5o7litpsj78s4whmrldsk1ec foreign key (actor_list_id) references actor;
alter table actor_content_list_actor_list add constraint FK431mogrem339r8sk1d885mya foreign key (actor_content_list_id) references actor_content_list;
alter table movie_actor_list add constraint FKg37y89dm8e8vdox05j16k6fi2 foreign key (actor_list_id) references actor;
alter table movie_actor_list add constraint FKg1xdxswaoarr5vegc1oa9jgxv foreign key (movie_id) references movie;
alter table movie_country_list add constraint FKpofouwmducfnymnevx9tq31xm foreign key (movie_id) references movie;
alter table movie_director_list add constraint FKnet2h89rjd2oyr48ibmawjf89 foreign key (director_list_id) references actor;
alter table movie_director_list add constraint FKki8kugvwhq0b7yy237v3a8b8k foreign key (movie_id) references movie;
alter table movie_genre_list add constraint FKatvkfhllpifbwi71s1xvm2q8p foreign key (movie_id) references movie;
alter table movie_writer_list add constraint FKruwf5nvo2ttblrebdjdogtet1 foreign key (writer_list_id) references actor;
alter table movie_writer_list add constraint FKlirnrdpyt6g9cnp7i7ou9y2m foreign key (movie_id) references movie;
alter table movie_content_list_movies add constraint FKjbns0uhks3d0sdw1bpqrdxol0 foreign key (movies_id) references movie;
alter table movie_content_list_movies add constraint FK1okkx9bssqymw76lxxpkam1ko foreign key (movie_content_list_id) references movie_content_list;
alter table users_actor_list add constraint FKd158cu5iu8jl3wcyas9nvik3l foreign key (actor_list_id) references actor_content_list;
alter table users_actor_list add constraint FKc8enlq40mruei4adaw8vet5dm foreign key (user_id) references users;
alter table users_movie_lists add constraint FKgjbsrph4u3aop4ifbkb8v197g foreign key (movie_lists_id) references movie_content_list;
alter table users_movie_lists add constraint FKspmuxdu8cj3cc9khu8jis8wf5 foreign key (user_id) references users