ALTER TABLE DYEXTN_COMBOBOX ADD(NO_OF_COLUMNS number(10,0));


create table DYEXTN_ABSTRACT_FORM_CONTEXT (
	IDENTIFIER number(19,0) not null,
	FORM_LABEL varchar(255),
	CONTAINER_ID number(19,0),
	HIDE_FORM number(1,0) default 0,
	ACTIVITY_STATUS varchar(10),
	primary key (IDENTIFIER)
);

create table DYEXTN_ABSTRACT_RECORD_ENTRY (
	IDENTIFIER number(19,0) not null,
	ABSTRACT_FORM_CONTEXT_ID number(19,0),
	MODIFIED_DATE date,
	MODIFIED_BY varchar(255),
	ACTIVITY_STATUS varchar(10),
	primary key (IDENTIFIER)
);

alter table DYEXTN_ABSTRACT_RECORD_ENTRY add constraint FKA74A5FE4DDFB1974 foreign key (ABSTRACT_FORM_CONTEXT_ID) references DYEXTN_ABSTRACT_FORM_CONTEXT (IDENTIFIER);
alter table DYEXTN_CATEGORY add USER_ID number(19,0);
alter table DYEXTN_CATEGORY_ATTRIBUTE ADD(IS_SRC_FOR_CAL_ATTR number(1,0) default 0);
alter table DYEXTN_FORMULA modify expression varchar2(4000);

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
