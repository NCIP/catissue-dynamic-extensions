
drop table if exists INTER_MODEL_ASSOCIATION;
drop table if exists INTRA_MODEL_ASSOCIATION;
drop table if exists ASSOCIATION;

/* Possible values for ASSOCIATION_TYPE are 1 and 2
ASSOCIATION_TYPE = 1 represents INTER_MODEL_ASSOCIATION.
ASSOCIATION_TYPE = 2 represents INTRA_MODEL_ASSOCIATION.
*/     
create table ASSOCIATION(
    ASSOCIATION_ID    bigint    not null,
    ASSOCIATION_TYPE  INT(8)    not null ,
    primary key (ASSOCIATION_ID)
);

create table INTER_MODEL_ASSOCIATION(
    ASSOCIATION_ID      bigint  not null references ASSOCIATION(ASSOCIATION_ID),
    LEFT_ENTITY_ID      bigint  not null,
    LEFT_ATTRIBUTE_ID   bigint  not null,
    RIGHT_ENTITY_ID     bigint  not null,
    RIGHT_ATTRIBUTE_ID  bigint  not null,
    primary key (ASSOCIATION_ID) 
);
create table INTRA_MODEL_ASSOCIATION(
    ASSOCIATION_ID    bigint    not null references ASSOCIATION(ASSOCIATION_ID),
    DE_ASSOCIATION_ID bigint    not null,
    primary key (ASSOCIATION_ID) 
);


drop table if exists ID_TABLE;

create table ID_TABLE(
    NEXT_ASSOCIATION_ID    bigint    not null,
    primary key (NEXT_ASSOCIATION_ID)
);

insert into ID_TABLE(NEXT_ASSOCIATION_ID) value(1);

commit;