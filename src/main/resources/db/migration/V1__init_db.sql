create table homework
(
    id           int8 not null,
    status       varchar(255),
    description  varchar(255),
    homework_url varchar(255),
    mentor_id    int8,
    intern_id    int8,
    primary key (id)
);

create table user_data
(
    id          int8 not null,
    bd_type     varchar(10),
    telegram_id varchar(255),
    username    varchar(255),
    name        varchar(255),
    last_name   varchar(255),
    patronymic  varchar(255),
    fio         varchar(255),
    role        varchar(255),
    status      varchar(255),
    primary key (id)
);

create table internship
(
    id          int8 not null,
    name        varchar(255),
    description varchar(255),
    check_word  varchar(255),
    chat_id     varchar(255),
    status      varchar(255),
    primary key (id)
);

create table user_internship
(
    user_id       int8 not null,
    internship_id int8 not null
);

alter table if exists user_internship
    add constraint fk_user_internship
    foreign key (user_id) references user_data;

alter table if exists user_internship
    add constraint fk_internship_user
    foreign key (internship_id) references internship;

alter table if exists homework
    add constraint fk_homework_mentor
    foreign key (mentor_id) references user_data;

alter table if exists homework
    add constraint fk_homework_intern
    foreign key (intern_id) references user_data;
