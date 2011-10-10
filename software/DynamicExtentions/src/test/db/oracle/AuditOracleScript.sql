create table CATISSUE_AUDIT_EVENT (
   IDENTIFIER number(19,0) not null ,
   IP_ADDRESS varchar(20),
   EVENT_TIMESTAMP date,
   USER_ID number(19,0),
   COMMENTS varchar2(500),
   EVENT_TYPE varchar(20),
   primary key (IDENTIFIER)
);
create table CATISSUE_AUDIT_EVENT_LOG (
   IDENTIFIER number(19,0) not null ,
   AUDIT_EVENT_ID number(19,0),
   primary key (IDENTIFIER)
);
create table CATISSUE_AUDIT_EVENT_DETAILS (
   IDENTIFIER number(19,0) not null ,
   ELEMENT_NAME varchar(150),
   AUDIT_EVENT_LOG_ID number(19,0),
   CURRENT_VALUE CLOB DEFAULT NULL,
   PREVIOUS_VALUE CLOB DEFAULT NULL,
   primary key (IDENTIFIER)
);
CREATE TABLE CATISSUE_DATA_AUDIT_EVENT_LOG
(
	IDENTIFIER NUMBER NOT NULL,
	OBJECT_IDENTIFIER NUMBER,
	OBJECT_NAME VARCHAR(50),
	PARENT_LOG_ID NUMBER,
	PRIMARY KEY (IDENTIFIER)
);
CREATE TABLE CATISSUE_LOGIN_AUDIT_EVENT_LOG
(
	IDENTIFIER NUMBER NOT NULL,
	LOGIN_TIMESTAMP DATE,
	USER_LOGIN_ID VARCHAR(20),
	LOGIN_SOURCE_ID NUMBER,
	LOGIN_IP_ADDRESS VARCHAR(20),
	IS_LOGIN_SUCCESSFUL VARCHAR(10),
	PRIMARY KEY (IDENTIFIER)
) ;
create table CATISSUE_AUDIT_EVENT_QUERY_LOG (
   IDENTIFIER number(19,0) not null,
   QUERY_DETAILS clob,
   AUDIT_EVENT_ID number(19,0),
   QUERY_ID number(20,0) DEFAULT NULL,
   TEMP_TABLE_NAME varchar2(150) DEFAULT NULL,
   IF_TEMP_TABLE_DELETED number(1,0) DEFAULT 0,
   ROOT_ENTITY_NAME VARCHAR2(150 BYTE) DEFAULT null,
   COUNT_OF_ROOT_RECORDS NUMBER(20,0) DEFAULT null,
   primary key (IDENTIFIER)
);

ALTER TABLE CATISSUE_DATA_AUDIT_EVENT_LOG ADD CONSTRAINT FK5C07745DC62F96A411
FOREIGN KEY (IDENTIFIER) REFERENCES CATISSUE_AUDIT_EVENT_LOG (IDENTIFIER);

ALTER TABLE CATISSUE_DATA_AUDIT_EVENT_LOG ADD CONSTRAINT FK5C07745DC62F96A412
FOREIGN KEY (PARENT_LOG_ID) REFERENCES CATISSUE_DATA_AUDIT_EVENT_LOG (IDENTIFIER);

alter table CATISSUE_AUDIT_EVENT_QUERY_LOG add constraint FK62DC439DBC7298A9 foreign key (AUDIT_EVENT_ID) references CATISSUE_AUDIT_EVENT ;
alter table CATISSUE_AUDIT_EVENT_DETAILS  add constraint FK5C07745D34FFD77F foreign key (AUDIT_EVENT_LOG_ID) references CATISSUE_AUDIT_EVENT_LOG  ;
alter table CATISSUE_AUDIT_EVENT_LOG  add constraint FK8BB672DF77F0B904 foreign key (AUDIT_EVENT_ID) references CATISSUE_AUDIT_EVENT  ;

create sequence CATISSUE_AUDIT_EVENT_LOG_SEQ;
create sequence CATISSUE_AUDIT_EVENT_DET_SEQ;
create sequence CATISSUE_AUDIT_EVENT_PARAM_SEQ;
create sequence CATISSUE_AUDIT_EVENT_SEQ;
create sequence CATISSUE_AUDIT_EVENT_QUERY_SEQ;
commit;
