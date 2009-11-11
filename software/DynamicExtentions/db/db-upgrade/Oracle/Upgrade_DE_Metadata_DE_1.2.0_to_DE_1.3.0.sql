create table DYEXTN_SKIP_LOGIC_ATTRIBUTE (
	IDENTIFIER number(19,0) not null,
	SOURCE_SKIP_LOGIC_ID number(19,0),
	TARGET_SKIP_LOGIC_ID number(19,0),
	PERM_VALUE_ID number(19,0),
	CAT_ATTR_ID number(19,0),
	primary key (IDENTIFIER)
);
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add constraint FK722F03989F433752 foreign key (SOURCE_SKIP_LOGIC_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add constraint FK722F039889907A48 foreign key (TARGET_SKIP_LOGIC_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add constraint FK722F03985CC8694E foreign key (IDENTIFIER) references DYEXTN_BASE_ABSTRACT_ATTRIBUTE;
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add constraint FK722F0398B96D382 foreign key (PERM_VALUE_ID) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add constraint FK32982F0498B96D234 foreign key (CAT_ATTR_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
ALTER TABLE  DYEXTN_CONTROL ADD(SKIP_LOGIC number(1,0) default 0);
ALTER TABLE  DYEXTN_CATEGORY_ATTRIBUTE ADD(IS_SKIP_LOGIC number(1,0) default 0);
ALTER TABLE  DYEXTN_DATA_ELEMENT ADD(SKIP_LOGIC_ATTRIBUTE_ID number(19,0));
ALTER TABLE  DYEXTN_PERMISSIBLE_VALUE ADD(SKIP_LOGIC_ATTRIBUTE_ID number(19,0));
ALTER TABLE  DYEXTN_PERMISSIBLE_VALUE ADD(SKIP_LOGIC_CAT_ATTR_ID number(19,0));
ALTER TABLE  DYEXTN_CONTROL ADD(SKIP_LOGIC_TARGET_CONTROL number(1,0) default 0);
ALTER TABLE  DYEXTN_CONTROL ADD(SHOW_HIDE number(1,0) default 0);
ALTER TABLE  DYEXTN_CONTROL ADD(SELECTIVE_READ_ONLY number(1,0) default 0);

alter table DYEXTN_DATA_ELEMENT add constraint FKB1153E4791A6280 foreign key (SKIP_LOGIC_ATTRIBUTE_ID) references DYEXTN_SKIP_LOGIC_ATTRIBUTE;
alter table DYEXTN_PERMISSIBLE_VALUE add constraint FK136264E0791A6280 foreign key (SKIP_LOGIC_ATTRIBUTE_ID) references DYEXTN_SKIP_LOGIC_ATTRIBUTE;
alter table DYEXTN_PERMISSIBLE_VALUE add constraint FK136264B0791V635350 foreign key (SKIP_LOGIC_CAT_ATTR_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
ALTER TABLE DYEXTN_DATEPICKER ADD(SHOWCALENDAR number(1,0) default 1);

create table DYEXTN_MULTISELECT_CHECK_BOX (
	IDENTIFIER number(19,0) not null,
	MULTISELECT number(1,0),
	primary key (IDENTIFIER)
);
alter table DYEXTN_MULTISELECT_CHECK_BOX add constraint FK4312896DBF67AB26 foreign key (IDENTIFIER) references DYEXTN_SELECT_CONTROL;
ALTER TABLE DYEXTN_CONTROL ADD(SOURCE_CONTROL_ID number(19,0));
alter table DYEXTN_CONTROL add constraint FK353682F045346D34 foreign key (SOURCE_CONTROL_ID) references DYEXTN_CONTROL;

/*Bug #13745 : Rename 'entry number' to 'visit number'*/
UPDATE DYEXTN_ABSTRACT_METADATA SET NAME='visitNumber' WHERE NAME='entryNumber';

update dyextn_date_type_info set format = 'DateOnly' where format like 'MM-dd-yyyy';
update dyextn_date_type_info set format = 'DateAndTime' where format like 'MM-dd-yyyy HH:mm';
update dyextn_date_type_info set format = 'DateOnly' where format is null;
update dyextn_date_type_info set format = 'DateAndTime' where format like 'yyyy-MM-dd-HH24.mm.ss.SSS';
update dyextn_date_type_info set format = 'MonthAndYear' where format like 'MM-yyyy';
update dyextn_date_type_info set format = 'YearOnly' where format like 'yyyy';

commit;