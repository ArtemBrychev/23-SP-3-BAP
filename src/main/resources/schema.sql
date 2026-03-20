create table if not exists url (
	id SERIAL primary key,
	url_old VARCHAR(600)  unique not null,
	url_new VARCHAR(255)
);

create index if not exists index_on_url_old
on url(url_old);