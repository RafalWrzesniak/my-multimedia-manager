alter table actor add constraint UK_pl7mci1bl2t3h2qv1jtyx01sr unique (filmweb_url);
alter table actor add constraint UK_krnk9joviqbim83s2qbv0p1w7 unique (imdb_id);
alter table movie add constraint UK_eoh10u1dqe9qte40nuhqj9d5d unique (filmweb_url);
alter table movie add constraint UK_gk23fo99ijqjqbe7fe75a7nva unique (imdb_id);