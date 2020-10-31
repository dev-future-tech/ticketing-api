create table assignees (
    user_id bigint generated always as identity primary key,
    username varchar(120) not null,
    email_addr varchar(200) null,
    data_created timestamp not null default now()
);

create table tickets (
    ticket_id bigint generated always as identity primary key,
    summary varchar(120) not null,
    description text null,
    assignee_id bigint null,
    date_created timestamp not null default now()
);

alter table tickets add constraint fk_assignee foreign key (assignee_id) references assignees(user_id);
