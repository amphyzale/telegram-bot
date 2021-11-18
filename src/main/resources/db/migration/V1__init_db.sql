create table homework
(
    id           int8 not null,
    status       varchar(255),
    description  varchar(255),
    homework_url varchar(255),
    intern_id    int8,
    mentor_id    int8,
    primary key (id)
);

create table intern
(
    id          int8 not null,
    telegram_id varchar(255),
    name        varchar(255),
    last_name   varchar(255),
    patronymic  varchar(255),
    fio         varchar(255),
    status      varchar(255),
    primary key (id)
);

create table internship
(
    id                int8 not null,
    name              varchar(255),
    intern_check_word varchar(255),
    mentor_check_word varchar(255),
    status            varchar(255),
    primary key (id)
);

create table mentor
(
    id          int8 not null,
    telegram_id varchar(255),
    name        varchar(255),
    last_name   varchar(255),
    patronymic  varchar(255),
    fio         varchar(255),
    primary key (id)
);

create table mentor_internship
(
    mentor_id     int8 not null,
    internship_id int8 not null
);

create table intern_internship
(
    intern_id     int8 not null,
    internship_id int8 not null
);

alter table if exists mentor_internship
    add constraint fk_mentor_internship
    foreign key (mentor_id) references mentor;

alter table if exists mentor_internship
    add constraint fk_internship_mentor
    foreign key (internship_id) references internship;

alter table if exists intern_internship
    add constraint fk_intern_internship
    foreign key (intern_id) references intern;

alter table if exists intern_internship
    add constraint fk_internship_intern
    foreign key (internship_id) references internship;

alter table if exists homework
    add constraint fk_homework_intern
    foreign key (intern_id) references intern;

alter table if exists homework
    add constraint fk_homework_mentor
    foreign key (mentor_id) references mentor;