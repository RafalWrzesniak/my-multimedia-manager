alter table actor_directed_movies drop constraint FKi4b0k4ld17p8uwni708u1qb96;
alter table actor_played_in_movies drop constraint FKob46mu2dbekg1cxib7oeqr4uy;
alter table actor_wrote_movies drop constraint FKnhu6l6tgnntitffpp34a2uhyn;
alter table author_written_books drop constraint FKpkg95abh9m75tmjjwmk5bcrmr;
alter table book_content_list_content_list drop constraint FKer0qr5l3sm9b2ouagtv0fetdk;
alter table movie_content_list_content_list drop constraint FKcap6ok5jybamv8c27jhqhiior;

alter table actor_directed_movies add constraint FKi4b0k4ld17p8uwni708u1qb96 foreign key (directed_movies_id) references movie ON DELETE CASCADE;
alter table actor_played_in_movies add constraint FKob46mu2dbekg1cxib7oeqr4uy foreign key (played_in_movies_id) references movie ON DELETE CASCADE;
alter table actor_wrote_movies add constraint FKnhu6l6tgnntitffpp34a2uhyn foreign key (wrote_movies_id) references movie ON DELETE CASCADE;
alter table author_written_books add constraint FKpkg95abh9m75tmjjwmk5bcrmr foreign key (written_books_id) references book ON DELETE CASCADE;
alter table book_content_list_content_list add constraint FKer0qr5l3sm9b2ouagtv0fetdk foreign key (content_list_id) references book ON DELETE CASCADE;
alter table movie_content_list_content_list add constraint FKcap6ok5jybamv8c27jhqhiior foreign key (content_list_id) references movie ON DELETE CASCADE;