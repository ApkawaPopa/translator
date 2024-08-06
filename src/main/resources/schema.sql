create table if not exists translation_data
(
    id              serial primary key,
    user_address    varchar(15),
    text            text,
    translated_text text
);