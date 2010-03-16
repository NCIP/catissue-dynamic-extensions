ALTER TABLE DYEXTN_COMBOBOX ADD column NO_OF_COLUMNS integer;
create table DYEXTN_ABSTRACT_FORM_CONTEXT (IDENTIFIER bigint not null auto_increment, FORM_LABEL varchar(255), CONTAINER_ID bigint, HIDE_FORM bit default 0, ACTIVITY_STATUS varchar(10), primary key (IDENTIFIER));
create table DYEXTN_ABSTRACT_RECORD_ENTRY (IDENTIFIER bigint not null auto_increment, MODIFIED_DATE date, MODIFIED_BY varchar(255), ACTIVITY_STATUS varchar(10), ABSTRACT_FORM_CONTEXT_ID bigint, primary key (IDENTIFIER));
alter table DYEXTN_ABSTRACT_RECORD_ENTRY add index FKA74A5FE4DDFB1974 (ABSTRACT_FORM_CONTEXT_ID), add constraint FKA74A5FE4DDFB1974 foreign key (ABSTRACT_FORM_CONTEXT_ID) references DYEXTN_ABSTRACT_FORM_CONTEXT (IDENTIFIER);
alter table DYEXTN_CATEGORY add USER_ID bigint;
ALTER TABLE DYEXTN_CATEGORY_ATTRIBUTE ADD IS_SRC_FOR_CAL_ATTR bit default 0;
alter table DYEXTN_FORMULA modify expression varchar(4000);

update dyextn_constraintkey_prop set PRIMARY_ATTRIBUTE_ID = null where SRC_CONSTRAINT_KEY_ID in
(
select identifier from dyextn_constraint_properties where CATEGORY_ASSOCIATION_ID in
(select identifier from dyextn_category_association)
) and PRIMARY_ATTRIBUTE_ID is not null;

update dyextn_constraintkey_prop set PRIMARY_ATTRIBUTE_ID = null where TGT_CONSTRAINT_KEY_ID in
(
select identifier from dyextn_constraint_properties where CATEGORY_ASSOCIATION_ID in
(select identifier from dyextn_category_association)
) and PRIMARY_ATTRIBUTE_ID is not null;

commit;
