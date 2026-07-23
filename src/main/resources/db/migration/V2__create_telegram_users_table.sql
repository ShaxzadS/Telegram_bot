create table telegram_users (
    telegram_user_id bigint primary key,
    chat_id bigint not null,
    username varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    phone_number varchar(32) not null,
    registered_at timestamp with time zone not null
);

create index idx_telegram_users_phone_number on telegram_users (phone_number);
