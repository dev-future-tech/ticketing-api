create table assignees (
    user_id bigint generated always as identity primary key,
    username varchar(120) not null,
    email_addr varchar(200) null,
    data_created timestamp not null default now(),
    global_id varchar(36) not null,
    region varchar(25) not null
);

create unique index idx_region_global_id on assignees (global_id, region);

create table tickets (
    ticket_id bigint generated always as identity primary key,
    summary varchar(120) not null,
    description text null,
    assignee_url varchar(250) null,
    date_created timestamp not null default now(),
    global_id varchar(36) not null,
    region varchar(25) not null
);

create unique index idx_tickets_region_global_id on tickets (global_id, region);
