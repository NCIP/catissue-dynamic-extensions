create table DE_COLL_ATTR_RECORD_VALUES (
   IDENTIFIER number(19,0) not null,
   RECORD_VALUE varchar2(4000),
   COLLECTION_ATTR_RECORD_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DE_FILE_ATTR_RECORD_VALUES (
   IDENTIFIER number(19,0) not null,
   CONTENT_TYPE varchar2(255),
   FILE_CONTENT blob,
   FILE_NAME varchar2(255),
   RECORD_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DE_OBJECT_ATTR_RECORD_VALUES (
   IDENTIFIER number(19,0) not null,
   CLASS_NAME varchar2(255),
   OBJECT_CONTENT blob,
   RECORD_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_ABSTRACT_ENTITY (
   id number(19,0) not null,
   primary key (id)
);
create table DYEXTN_ABSTRACT_METADATA (
   IDENTIFIER number(19,0) not null,
   CREATED_DATE date,
   DESCRIPTION varchar2(1000),
   LAST_UPDATED date,
   NAME varchar2(1000),
   PUBLIC_ID varchar2(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_ABSTR_CONTAIN_CTR (
   IDENTIFIER number(19,0) not null,
   CONTAINER_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_ASSOCIATION (
   IDENTIFIER number(19,0) not null,
   DIRECTION varchar2(255),
   TARGET_ENTITY_ID number(19,0),
   SOURCE_ROLE_ID number(19,0),
   TARGET_ROLE_ID number(19,0),
   IS_SYSTEM_GENERATED number(1,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_ASSO_DISPLAY_ATTR (
   IDENTIFIER number(19,0) not null,
   SEQUENCE_NUMBER number(10,0),
   DISPLAY_ATTRIBUTE_ID number(19,0),
   SELECT_CONTROL_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_ATTRIBUTE (
   IDENTIFIER number(19,0) not null,
   ENTIY_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_ATTRIBUTE_RECORD (
   IDENTIFIER number(19,0) not null,
   ENTITY_ID number(19,0),
   ATTRIBUTE_ID number(19,0),
   RECORD_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_ATTRIBUTE_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   PRIMITIVE_ATTRIBUTE_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_BARR_CONCEPT_VALUE (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_BASE_ABSTRACT_ATTRIBUTE (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_BOOLEAN_CONCEPT_VALUE (
   IDENTIFIER number(19,0) not null,
   VALUE number(1,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_BOOLEAN_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_BYTE_ARRAY_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   CONTENT_TYPE varchar2(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_CADSRDE (
   IDENTIFIER number(19,0) not null,
   PUBLIC_ID varchar2(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_CADSR_VALUE_DOMAIN_INFO (
   IDENTIFIER number(19,0) not null,
   DATATYPE varchar2(255),
   NAME varchar2(255),
   TYPE varchar2(255),
   PRIMITIVE_ATTRIBUTE_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_CATEGORY (
   IDENTIFIER number(19,0) not null,
   ROOT_CATEGORY_ELEMENT number(19,0),
   CATEGORY_ENTITY_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_CATEGORY_ASSOCIATION (
   IDENTIFIER number(19,0) not null,
   CATEGORY_ENTIY_ID number(19,0),
   CATEGORY_ENTITY_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_CATEGORY_ATTRIBUTE (
   IDENTIFIER number(19,0) not null,
   ABSTRACT_ATTRIBUTE_ID number(19,0),
   CATEGORY_ENTIY_ID number(19,0),
   CATEGORY_ENTITY_ID number(19,0),
   IS_VISIBLE number(1,0),
   IS_RELATTRIBUTE number(1,0),
   IS_CAL_ATTRIBUTE number(1,0),
   CAL_CATEGORY_ATTR_ID number(19,0),
   CAL_DEPENDENT_CATEGORY_ATTR_ID number(19,0),
   IS_SKIP_LOGIC number(1,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_CATEGORY_ENTITY (
   IDENTIFIER number(19,0) not null,
   NUMBER_OF_ENTRIES number(10,0),
   ENTITY_ID number(19,0),
   OWN_PARENT_CATEGORY_ENTITY_ID number(19,0),
   TREE_PARENT_CATEGORY_ENTITY_ID number(19,0),
   CATEGORY_ASSOCIATION_ID number(19,0),
   PARENT_CATEGORY_ENTITY_ID number(19,0),
   REL_ATTR_CAT_ENTITY_ID number(19,0),
   IS_CREATETABLE number(1,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_CAT_ASSO_CTL (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_CHECK_BOX (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_COLUMN_PROPERTIES (
   IDENTIFIER number(19,0) not null,
   CONSTRAINT_NAME varchar2(255),
   PRIMITIVE_ATTRIBUTE_ID number(19,0),
   CATEGORY_ATTRIBUTE_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_COMBOBOX (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_CONSTRAINT_PROPERTIES (
   IDENTIFIER number(19,0) not null,
   SOURCE_ENTITY_KEY varchar2(255),
   TARGET_ENTITY_KEY varchar2(255),
   SRC_CONSTRAINT_NAME varchar2(255),
   TARGET_CONSTRAINT_NAME varchar2(255),
   ASSOCIATION_ID number(19,0),
   CATEGORY_ASSOCIATION_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_CONTAINER (
   IDENTIFIER number(19,0) not null,
   BUTTON_CSS varchar2(255),
   CAPTION varchar2(800),
   ABSTRACT_ENTITY_ID number(19,0),
   MAIN_TABLE_CSS varchar2(255),
   REQUIRED_FIELD_INDICATOR varchar2(255),
   REQUIRED_FIELD_WARNING_MESSAGE varchar2(255),
   TITLE_CSS varchar2(255),
   BASE_CONTAINER_ID number(19,0),
   ADD_CAPTION number(1,0),
   ENTITY_GROUP_ID number(19,0),
   PARENT_CONTAINER_ID number(19,0),
   VIEW_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_CONTAINMENT_CONTROL (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_CONTROL (
   IDENTIFIER number(19,0) not null,
   SHOW_LABEL number(1,0),
   CAPTION varchar2(800),
   IS_CALCULATED number(1,0),
   CSS_CLASS varchar2(255),
   HIDDEN number(1,0),
   NAME varchar2(255),
   SEQUENCE_NUMBER number(10,0),
   TOOLTIP varchar2(255),
   CONTAINER_ID number(19,0),
   BASE_ABST_ATR_ID number(19,0),
   READ_ONLY number(1,0),
   HEADING varchar2(255),
   yPosition number(10,0),
   SKIP_LOGIC number(1,0),
   SKIP_LOGIC_TARGET_CONTROL number(1,0),
   SHOW_HIDE number(1,0),
   SELECTIVE_READ_ONLY number(1,0),
   SOURCE_CONTROL_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_DATABASE_PROPERTIES (
   IDENTIFIER number(19,0) not null,
   NAME varchar2(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_DATA_ELEMENT (
   IDENTIFIER number(19,0) not null,
   CATEGORY_ATTRIBUTE_ID number(19,0),
   ATTRIBUTE_TYPE_INFO_ID number(19,0),
   SKIP_LOGIC_ATTRIBUTE_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_DATA_GRID (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_DATEPICKER (
   IDENTIFIER number(19,0) not null,
   DATE_VALUE_TYPE varchar2(255),
   SHOWCALENDAR number(1,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_DATE_CONCEPT_VALUE (
   IDENTIFIER number(19,0) not null,
   VALUE timestamp,
   primary key (IDENTIFIER)
);
create table DYEXTN_DATE_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   FORMAT varchar2(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_DOUBLE_CONCEPT_VALUE (
   IDENTIFIER number(19,0) not null,
   VALUE double precision,
   primary key (IDENTIFIER)
);
create table DYEXTN_DOUBLE_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_ENTITY (
   IDENTIFIER number(19,0) not null,
   DATA_TABLE_STATE number(10,0),
   ENTITY_GROUP_ID number(19,0),
   IS_ABSTRACT number(1,0),
   PARENT_ENTITY_ID number(19,0),
   INHERITANCE_STRATEGY number(10,0),
   DISCRIMINATOR_COLUMN_NAME varchar2(255),
   DISCRIMINATOR_VALUE varchar2(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_ENTITY_GROUP (
   IDENTIFIER number(19,0) not null,
   LONG_NAME varchar2(255),
   SHORT_NAME varchar2(255),
   VERSION varchar2(255),
   IS_SYSTEM_GENERATED number(1,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_ENTITY_MAP (
   IDENTIFIER number(19,0) not null,
   CONTAINER_ID number(19,0),
   STATUS varchar2(10),
   STATIC_ENTITY_ID number(19,0),
   CREATED_DATE date,
   CREATED_BY varchar2(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_ENTITY_MAP_CONDNS (
   IDENTIFIER number(19,0) not null,
   STATIC_RECORD_ID number(19,0),
   TYPE_ID number(19,0),
   FORM_CONTEXT_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_ENTITY_MAP_RECORD (
   IDENTIFIER number(19,0) not null,
   FORM_CONTEXT_ID number(19,0),
   STATIC_ENTITY_RECORD_ID number(19,0),
   STATUS varchar2(10),
   DYNAMIC_ENTITY_RECORD_ID number(19,0),
   MODIFIED_DATE date,
   CREATED_DATE date,
   CREATED_BY varchar2(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_FILE_EXTENSIONS (
   IDENTIFIER number(19,0) not null,
   FILE_EXTENSION varchar2(255),
   ATTRIBUTE_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_FILE_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   MAX_FILE_SIZE float,
   primary key (IDENTIFIER)
);
create table DYEXTN_FILE_UPLOAD (
   IDENTIFIER number(19,0) not null,
   NO_OF_COLUMNS number(10,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_FLOAT_CONCEPT_VALUE (
   IDENTIFIER number(19,0) not null,
   VALUE float,
   primary key (IDENTIFIER)
);
create table DYEXTN_FLOAT_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_FORMULA (
   IDENTIFIER number(19,0) not null,
   EXPRESSION varchar2(800),
   CATEGORY_ATTRIBUTE_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_SKIP_LOGIC_ATTRIBUTE (
	IDENTIFIER number(19,0) not null,
	SOURCE_SKIP_LOGIC_ID number(19,0),
	TARGET_SKIP_LOGIC_ID number(19,0),
	PERM_VALUE_ID number(19,0),
    CAT_ATTR_ID number(19,0),
	primary key (IDENTIFIER)
);
create table DYEXTN_FORM_CONTEXT (
   IDENTIFIER number(19,0) not null,
   IS_INFINITE_ENTRY number(1,0),
   ENTITY_MAP_ID number(19,0),
   STUDY_FORM_LABEL varchar2(255),
   NO_OF_ENTRIES number(10,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_FORM_CTRL_NOTES (
   IDENTIFIER number(19,0) not null,
   NOTE varchar2(255),
   FORM_CONTROL_ID number(19,0),
   INSERTION_ORDER number(10,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_INTEGER_CONCEPT_VALUE (
   IDENTIFIER number(19,0) not null,
   VALUE number(10,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_INTEGER_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_LABEL (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_LIST_BOX (
   IDENTIFIER number(19,0) not null,
   MULTISELECT number(1,0),
   NO_OF_ROWS number(10,0),
   USE_AUTOCOMPLETE NUMBER(1,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_LONG_CONCEPT_VALUE (
   IDENTIFIER number(19,0) not null,
   VALUE number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_LONG_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_MULTISELECT_CHECK_BOX (
	IDENTIFIER number(19,0) not null,
	MULTISELECT number(1,0),
	primary key (IDENTIFIER)
);
create table DYEXTN_NUMERIC_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   MEASUREMENT_UNITS varchar2(255),
   DECIMAL_PLACES number(10,0),
   NO_DIGITS number(10,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_OBJECT_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_PATH (
   IDENTIFIER number(19,0) not null,
   CATEGORY_ENTITY_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_PATH_ASSO_REL (
   IDENTIFIER number(19,0) not null,
   PATH_ID number(19,0),
   ASSOCIATION_ID number(19,0),
   PATH_SEQUENCE_NUMBER number(10,0),
   SRC_INSTANCE_ID number(19,0),
   TGT_INSTANCE_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_PERMISSIBLE_VALUE (
   IDENTIFIER number(19,0) not null,
   DESCRIPTION varchar2(255),
   CATEGORY_ATTRIBUTE_ID number(19,0),
   ATTRIBUTE_TYPE_INFO_ID number(19,0),
   SKIP_LOGIC_ATTRIBUTE_ID number(19,0),
   SKIP_LOGIC_CAT_ATTR_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_PRIMITIVE_ATTRIBUTE (
   IDENTIFIER number(19,0) not null,
   IS_COLLECTION number(1,0),
   IS_IDENTIFIED number(1,0),
   IS_PRIMARY_KEY number(1,0),
   IS_NULLABLE number(1,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_RADIOBUTTON (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_ROLE (
   IDENTIFIER number(19,0) not null,
   ASSOCIATION_TYPE varchar2(255),
   MAX_CARDINALITY number(10,0),
   MIN_CARDINALITY number(10,0),
   NAME varchar2(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_RULE (
   IDENTIFIER number(19,0) not null,
   NAME varchar2(255),
   ATTRIBUTE_ID number(19,0),
   CATEGORY_ATTR_ID number(19,0),
   IS_IMPLICIT number(1,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_RULE_PARAMETER (
   IDENTIFIER number(19,0) not null,
   NAME varchar2(255),
   VALUE varchar2(255),
   RULE_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_SELECT_CONTROL (
   IDENTIFIER number(19,0) not null,
   SEPARATOR_STRING varchar2(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_SEMANTIC_PROPERTY (
   IDENTIFIER number(19,0) not null,
   CONCEPT_CODE varchar2(255),
   TERM varchar2(255),
   THESAURAS_NAME varchar2(255),
   SEQUENCE_NUMBER number(10,0),
   CONCEPT_DEFINITION varchar2(255),
   ABSTRACT_METADATA_ID number(19,0),
   ABSTRACT_VALUE_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_SHORT_CONCEPT_VALUE (
   IDENTIFIER number(19,0) not null,
   VALUE number(5,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_SHORT_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_SQL_AUDIT (
   IDENTIFIER number(19,0) not null,
   AUDIT_DATE date,
   QUERY_EXECUTED varchar2(4000),
   USER_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_STRING_CONCEPT_VALUE (
   IDENTIFIER number(19,0) not null,
   VALUE varchar2(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_STRING_TYPE_INFO (
   IDENTIFIER number(19,0) not null,
   MAX_SIZE number(10,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_TABLE_PROPERTIES (
   IDENTIFIER number(19,0) not null,
   CONSTRAINT_NAME varchar2(255),
   ABSTRACT_ENTITY_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_TAGGED_VALUE (
   IDENTIFIER number(19,0) not null,
   T_KEY varchar2(255),
   T_VALUE varchar2(255),
   ABSTRACT_METADATA_ID number(19,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_TEXTAREA (
   IDENTIFIER number(19,0) not null,
   TEXTAREA_COLUMNS number(10,0),
   TEXTAREA_ROWS number(10,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_TEXTFIELD (
   IDENTIFIER number(19,0) not null,
   NO_OF_COLUMNS number(10,0),
   IS_PASSWORD number(1,0),
   IS_URL number(1,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_USERDEFINED_DE (
   IDENTIFIER number(19,0) not null,
   IS_ORDERED number(1,0),
   primary key (IDENTIFIER)
);
create table DYEXTN_USERDEF_DE_VALUE_REL (
   USER_DEF_DE_ID number(19,0) not null,
   PERMISSIBLE_VALUE_ID number(19,0) not null,
   primary key (USER_DEF_DE_ID, PERMISSIBLE_VALUE_ID)
);
create table DYEXTN_VIEW (
   IDENTIFIER number(19,0) not null,
   NAME varchar2(255),
   primary key (IDENTIFIER)
);
create table dyextn_id_generator (
   id number(19,0) not null,
   next_available_id number(19,0),
   primary key (id)
);
alter table DE_COLL_ATTR_RECORD_VALUES add constraint FK847DA57775255CA5 foreign key (COLLECTION_ATTR_RECORD_ID) references DYEXTN_ATTRIBUTE_RECORD;
alter table DE_FILE_ATTR_RECORD_VALUES add constraint FKE68334E74EB991B2 foreign key (RECORD_ID) references DYEXTN_ATTRIBUTE_RECORD;
alter table DE_OBJECT_ATTR_RECORD_VALUES add constraint FK504EADC44EB991B2 foreign key (RECORD_ID) references DYEXTN_ATTRIBUTE_RECORD;
alter table DYEXTN_ABSTRACT_ENTITY add constraint FKA4A164E3D3027A30 foreign key (id) references DYEXTN_ABSTRACT_METADATA;
alter table DYEXTN_ABSTR_CONTAIN_CTR add constraint FK9EB9020A69935DD6 foreign key (CONTAINER_ID) references DYEXTN_CONTAINER;
alter table DYEXTN_ABSTR_CONTAIN_CTR add constraint FK9EB9020A40F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL;
alter table DYEXTN_ASSOCIATION add constraint FK1046842440738A50 foreign key (TARGET_ENTITY_ID) references DYEXTN_ENTITY;
alter table DYEXTN_ASSOCIATION add constraint FK1046842439780F7A foreign key (SOURCE_ROLE_ID) references DYEXTN_ROLE;
alter table DYEXTN_ASSOCIATION add constraint FK104684242BD842F0 foreign key (TARGET_ROLE_ID) references DYEXTN_ROLE;
alter table DYEXTN_ASSOCIATION add constraint FK104684246D19A21F foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE;
alter table DYEXTN_ASSO_DISPLAY_ATTR add constraint FKD12FD3827FD29CDD foreign key (SELECT_CONTROL_ID) references DYEXTN_SELECT_CONTROL;
alter table DYEXTN_ASSO_DISPLAY_ATTR add constraint FKD12FD38235D6E973 foreign key (DISPLAY_ATTRIBUTE_ID) references DYEXTN_PRIMITIVE_ATTRIBUTE;
alter table DYEXTN_ATTRIBUTE add constraint FK37F1E2FFF99EA906 foreign key (ENTIY_ID) references DYEXTN_ENTITY;
alter table DYEXTN_ATTRIBUTE add constraint FK37F1E2FF5CC8694E foreign key (IDENTIFIER) references DYEXTN_BASE_ABSTRACT_ATTRIBUTE;
alter table DYEXTN_ATTRIBUTE_RECORD add constraint FK9B20ED914AC41F7E foreign key (ENTITY_ID) references DYEXTN_ENTITY;
alter table DYEXTN_ATTRIBUTE_RECORD add constraint FK9B20ED914DC2CD16 foreign key (ATTRIBUTE_ID) references DYEXTN_PRIMITIVE_ATTRIBUTE;
alter table DYEXTN_ATTRIBUTE_TYPE_INFO add constraint FK62596D531333996E foreign key (PRIMITIVE_ATTRIBUTE_ID) references DYEXTN_PRIMITIVE_ATTRIBUTE;
alter table DYEXTN_BARR_CONCEPT_VALUE add constraint FK89D27DF74641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_BASE_ABSTRACT_ATTRIBUTE add constraint FK14BA6610728B19BE foreign key (IDENTIFIER) references DYEXTN_ABSTRACT_METADATA;
alter table DYEXTN_BOOLEAN_CONCEPT_VALUE add constraint FK57B6C4A64641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_BOOLEAN_TYPE_INFO add constraint FK28F1809FE5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO;
alter table DYEXTN_BYTE_ARRAY_TYPE_INFO add constraint FK18BDA73E5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO;
alter table DYEXTN_CADSRDE add constraint FK588A250953CC4A77 foreign key (IDENTIFIER) references DYEXTN_DATA_ELEMENT;
alter table DYEXTN_CADSR_VALUE_DOMAIN_INFO add constraint FK1C9AA3641333996E foreign key (PRIMITIVE_ATTRIBUTE_ID) references DYEXTN_PRIMITIVE_ATTRIBUTE;
alter table DYEXTN_CATEGORY add constraint FKD33DE81B854AC01B foreign key (CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY;
alter table DYEXTN_CATEGORY add constraint FKD33DE81B54A9F59D foreign key (ROOT_CATEGORY_ELEMENT) references DYEXTN_CATEGORY_ENTITY;
alter table DYEXTN_CATEGORY add constraint FKD33DE81B728B19BE foreign key (IDENTIFIER) references DYEXTN_ABSTRACT_METADATA;
alter table DYEXTN_CATEGORY_ASSOCIATION add constraint FK1B7C663D854AC01B foreign key (CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY;
alter table DYEXTN_CATEGORY_ASSOCIATION add constraint FK1B7C663DCAC769C5 foreign key (CATEGORY_ENTIY_ID) references DYEXTN_CATEGORY_ENTITY;
alter table DYEXTN_CATEGORY_ASSOCIATION add constraint FK1B7C663D5CC8694E foreign key (IDENTIFIER) references DYEXTN_BASE_ABSTRACT_ATTRIBUTE;
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B7758854AC01B foreign key (CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY;
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B7758A2CA8853 foreign key (CAL_DEPENDENT_CATEGORY_ATTR_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B77585697A453 foreign key (CAL_CATEGORY_ATTR_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B77584DC2CD16 foreign key (ABSTRACT_ATTRIBUTE_ID) references DYEXTN_ATTRIBUTE;
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B7758CAC769C5 foreign key (CATEGORY_ENTIY_ID) references DYEXTN_CATEGORY_ENTITY;
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B77585CC8694E foreign key (IDENTIFIER) references DYEXTN_BASE_ABSTRACT_ATTRIBUTE;
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887A52559D0 foreign key (PARENT_CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY;
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA8874AC41F7E foreign key (ENTITY_ID) references DYEXTN_ENTITY;
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887F5A32608 foreign key (REL_ATTR_CAT_ENTITY_ID) references DYEXTN_CATEGORY;
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887A8103C6F foreign key (TREE_PARENT_CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY;
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887FB6EB979 foreign key (CATEGORY_ASSOCIATION_ID) references DYEXTN_CATEGORY_ASSOCIATION;
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887D06EE657 foreign key (OWN_PARENT_CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY;
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887743AC3F2 foreign key (IDENTIFIER) references DYEXTN_ABSTRACT_ENTITY;
alter table DYEXTN_CAT_ASSO_CTL add constraint FK281FAB50C45A8CFA foreign key (IDENTIFIER) references DYEXTN_ABSTR_CONTAIN_CTR;
alter table DYEXTN_CHECK_BOX add constraint FK4EFF925740F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL;
alter table DYEXTN_COLUMN_PROPERTIES add constraint FK8FCE2B3F1333996E foreign key (PRIMITIVE_ATTRIBUTE_ID) references DYEXTN_PRIMITIVE_ATTRIBUTE;
alter table DYEXTN_COLUMN_PROPERTIES add constraint FK8FCE2B3F67F8B59 foreign key (CATEGORY_ATTRIBUTE_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
alter table DYEXTN_COLUMN_PROPERTIES add constraint FK8FCE2B3F3AB6A1D3 foreign key (IDENTIFIER) references DYEXTN_DATABASE_PROPERTIES;
alter table DYEXTN_COMBOBOX add constraint FKABBC649ABF67AB26 foreign key (IDENTIFIER) references DYEXTN_SELECT_CONTROL;
alter table DYEXTN_CONSTRAINT_PROPERTIES add constraint FK82886CD87EE87FF6 foreign key (ASSOCIATION_ID) references DYEXTN_ASSOCIATION;
alter table DYEXTN_CONSTRAINT_PROPERTIES add constraint FK82886CD8FB6EB979 foreign key (CATEGORY_ASSOCIATION_ID) references DYEXTN_CATEGORY_ASSOCIATION;
alter table DYEXTN_CONSTRAINT_PROPERTIES add constraint FK82886CD83AB6A1D3 foreign key (IDENTIFIER) references DYEXTN_DATABASE_PROPERTIES;
alter table DYEXTN_CONTAINER add constraint FK1EAB84E41DCC9E63 foreign key (ABSTRACT_ENTITY_ID) references DYEXTN_ABSTRACT_ENTITY;
alter table DYEXTN_CONTAINER add constraint FK1EAB84E4178865E foreign key (VIEW_ID) references DYEXTN_VIEW;
alter table DYEXTN_CONTAINER add constraint FK1EAB84E440FCA34B foreign key (PARENT_CONTAINER_ID) references DYEXTN_CONTAINER;
alter table DYEXTN_CONTAINER add constraint FK1EAB84E488C075EF foreign key (ENTITY_GROUP_ID) references DYEXTN_ENTITY_GROUP;
alter table DYEXTN_CONTAINER add constraint FK1EAB84E4BF901C84 foreign key (BASE_CONTAINER_ID) references DYEXTN_CONTAINER;
alter table DYEXTN_CONTAINMENT_CONTROL add constraint FK3F9D4AD3C45A8CFA foreign key (IDENTIFIER) references DYEXTN_ABSTR_CONTAIN_CTR;
alter table DYEXTN_CONTROL add constraint FK70FB5E807792A78F foreign key (BASE_ABST_ATR_ID) references DYEXTN_BASE_ABSTRACT_ATTRIBUTE;
alter table DYEXTN_CONTROL add constraint FK70FB5E8069935DD6 foreign key (CONTAINER_ID) references DYEXTN_CONTAINER;
alter table DYEXTN_CONTROL add constraint FK353682F045346D34 foreign key (SOURCE_CONTROL_ID) references DYEXTN_CONTROL;
alter table DYEXTN_DATA_ELEMENT add constraint FKB1153E4AA208204 foreign key (ATTRIBUTE_TYPE_INFO_ID) references DYEXTN_ATTRIBUTE_TYPE_INFO;
alter table DYEXTN_DATA_ELEMENT add constraint FKB1153E467F8B59 foreign key (CATEGORY_ATTRIBUTE_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
alter table DYEXTN_DATA_GRID add constraint FK233EB73E40F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL;
alter table DYEXTN_DATEPICKER add constraint FKFEADD19940F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL;
alter table DYEXTN_DATE_CONCEPT_VALUE add constraint FK45F598A64641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_DATE_TYPE_INFO add constraint FKFBA549FE5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO;
alter table DYEXTN_DOUBLE_CONCEPT_VALUE add constraint FKB94E64494641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_DOUBLE_TYPE_INFO add constraint FKC83869C2BA4AE008 foreign key (IDENTIFIER) references DYEXTN_NUMERIC_TYPE_INFO;
alter table DYEXTN_ENTITY add constraint FK8B24364088C075EF foreign key (ENTITY_GROUP_ID) references DYEXTN_ENTITY_GROUP;
alter table DYEXTN_ENTITY add constraint FK8B2436402264D629 foreign key (PARENT_ENTITY_ID) references DYEXTN_ENTITY;
alter table DYEXTN_ENTITY add constraint FK8B243640743AC3F2 foreign key (IDENTIFIER) references DYEXTN_ABSTRACT_ENTITY;
alter table DYEXTN_ENTITY_GROUP add constraint FK105DE7A0728B19BE foreign key (IDENTIFIER) references DYEXTN_ABSTRACT_METADATA;
alter table DYEXTN_ENTITY_MAP_CONDNS add constraint FK2A9D6029CFA08B13 foreign key (FORM_CONTEXT_ID) references DYEXTN_FORM_CONTEXT;
alter table DYEXTN_ENTITY_MAP_RECORD add constraint FK43A45013CFA08B13 foreign key (FORM_CONTEXT_ID) references DYEXTN_FORM_CONTEXT;
alter table DYEXTN_FILE_EXTENSIONS add constraint FKD49834FA56AF0834 foreign key (ATTRIBUTE_ID) references DYEXTN_FILE_TYPE_INFO;
alter table DYEXTN_FILE_TYPE_INFO add constraint FKA00F0EDE5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO;
alter table DYEXTN_FILE_UPLOAD add constraint FK2FAD41E740F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL;
alter table DYEXTN_FLOAT_CONCEPT_VALUE add constraint FK6785309A4641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_FLOAT_TYPE_INFO add constraint FK7E1C0693BA4AE008 foreign key (IDENTIFIER) references DYEXTN_NUMERIC_TYPE_INFO;
alter table DYEXTN_FORMULA add constraint FKFE34A8967F8B59 foreign key (CATEGORY_ATTRIBUTE_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
alter table DYEXTN_FORM_CONTEXT add constraint FKE56CCDB12B784475 foreign key (ENTITY_MAP_ID) references DYEXTN_ENTITY_MAP;
alter table DYEXTN_FORM_CTRL_NOTES add constraint FK7A0DA06B41D885B1 foreign key (FORM_CONTROL_ID) references DYEXTN_CONTROL;
alter table DYEXTN_INTEGER_CONCEPT_VALUE add constraint FKFBA33B3C4641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_INTEGER_TYPE_INFO add constraint FK5F9CB235BA4AE008 foreign key (IDENTIFIER) references DYEXTN_NUMERIC_TYPE_INFO;
alter table DYEXTN_LABEL add constraint FK787668D740F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL;
alter table DYEXTN_LIST_BOX add constraint FK208395A7BF67AB26 foreign key (IDENTIFIER) references DYEXTN_SELECT_CONTROL;
alter table DYEXTN_LONG_CONCEPT_VALUE add constraint FK3E1A6EF44641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_LONG_TYPE_INFO add constraint FK257281EDBA4AE008 foreign key (IDENTIFIER) references DYEXTN_NUMERIC_TYPE_INFO;
alter table DYEXTN_MULTISELECT_CHECK_BOX add constraint FK4312896DBF67AB26 foreign key (IDENTIFIER) references DYEXTN_SELECT_CONTROL;
alter table DYEXTN_NUMERIC_TYPE_INFO add constraint FK4DEC9544E5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO;
alter table DYEXTN_OBJECT_TYPE_INFO add constraint FK74819FB0E5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO;
alter table DYEXTN_PATH add constraint FKC26ADC2854AC01B foreign key (CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY;
alter table DYEXTN_PATH_ASSO_REL add constraint FK1E1260A57EE87FF6 foreign key (ASSOCIATION_ID) references DYEXTN_ASSOCIATION;
alter table DYEXTN_PATH_ASSO_REL add constraint FK1E1260A580C8F93E foreign key (PATH_ID) references DYEXTN_PATH;
alter table DYEXTN_PERMISSIBLE_VALUE add constraint FK136264E0AA208204 foreign key (ATTRIBUTE_TYPE_INFO_ID) references DYEXTN_ATTRIBUTE_TYPE_INFO;
alter table DYEXTN_PERMISSIBLE_VALUE add constraint FK136264E067F8B59 foreign key (CATEGORY_ATTRIBUTE_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
alter table DYEXTN_PRIMITIVE_ATTRIBUTE add constraint FKA9F765C76D19A21F foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE;
alter table DYEXTN_RADIOBUTTON add constraint FK16F5BA9040F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL;
alter table DYEXTN_RULE add constraint FKC27E09990F96714 foreign key (ATTRIBUTE_ID) references DYEXTN_ATTRIBUTE;
alter table DYEXTN_RULE_PARAMETER add constraint FK2256736395D4A5AE foreign key (RULE_ID) references DYEXTN_RULE;
alter table DYEXTN_SELECT_CONTROL add constraint FKDFEBB65740F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL;
alter table DYEXTN_SEMANTIC_PROPERTY add constraint FKD2A0B5B15EB60E90 foreign key (ABSTRACT_VALUE_ID) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_SEMANTIC_PROPERTY add constraint FKD2A0B5B19AEB0CA3 foreign key (ABSTRACT_METADATA_ID) references DYEXTN_ABSTRACT_METADATA;
alter table DYEXTN_SHORT_CONCEPT_VALUE add constraint FKC1945ABA4641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_SHORT_TYPE_INFO add constraint FK99540B3BA4AE008 foreign key (IDENTIFIER) references DYEXTN_NUMERIC_TYPE_INFO;
alter table DYEXTN_STRING_CONCEPT_VALUE add constraint FKADE7D8894641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_STRING_TYPE_INFO add constraint FKDA35FE02E5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO;
alter table DYEXTN_TABLE_PROPERTIES add constraint FKE608E0811DCC9E63 foreign key (ABSTRACT_ENTITY_ID) references DYEXTN_ABSTRACT_ENTITY;
alter table DYEXTN_TABLE_PROPERTIES add constraint FKE608E0813AB6A1D3 foreign key (IDENTIFIER) references DYEXTN_DATABASE_PROPERTIES;
alter table DYEXTN_TAGGED_VALUE add constraint FKF79D055B9AEB0CA3 foreign key (ABSTRACT_METADATA_ID) references DYEXTN_ABSTRACT_METADATA;
alter table DYEXTN_TEXTAREA add constraint FK946EE25740F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL;
alter table DYEXTN_TEXTFIELD add constraint FKF9AFC85040F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL;
alter table DYEXTN_USERDEFINED_DE add constraint FK630761FF53CC4A77 foreign key (IDENTIFIER) references DYEXTN_DATA_ELEMENT;
alter table DYEXTN_USERDEF_DE_VALUE_REL add constraint FK3EE58DCF5521B106 foreign key (USER_DEF_DE_ID) references DYEXTN_USERDEFINED_DE;
alter table DYEXTN_USERDEF_DE_VALUE_REL add constraint FK3EE58DCF49BDD67 foreign key (PERMISSIBLE_VALUE_ID) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add constraint FK722F03989F433752 foreign key (SOURCE_SKIP_LOGIC_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add constraint FK722F039889907A48 foreign key (TARGET_SKIP_LOGIC_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add constraint FK722F03985CC8694E foreign key (IDENTIFIER) references DYEXTN_BASE_ABSTRACT_ATTRIBUTE;
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add constraint FK722F0398B96D382 foreign key (PERM_VALUE_ID) references DYEXTN_PERMISSIBLE_VALUE;
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add constraint FK32982F0498B96D234 foreign key (CAT_ATTR_ID) references DYEXTN_CATEGORY_ATTRIBUTE;
alter table DYEXTN_DATA_ELEMENT add constraint FKB1153E4791A6280 foreign key (SKIP_LOGIC_ATTRIBUTE_ID) references DYEXTN_SKIP_LOGIC_ATTRIBUTE;
alter table DYEXTN_PERMISSIBLE_VALUE add constraint FK136264E0791A6280 foreign key (SKIP_LOGIC_ATTRIBUTE_ID) references DYEXTN_SKIP_LOGIC_ATTRIBUTE;
alter table DYEXTN_PERMISSIBLE_VALUE add constraint FK136264B0791V635350 foreign key (SKIP_LOGIC_CAT_ATTR_ID) references DYEXTN_CATEGORY_ATTRIBUTE;

create sequence DE_ATTR_REC_SEQ;
create sequence DE_COLL_ATTR_REC_VALUES_SEQ;
create sequence DE_FILE_ATTR_REC_VALUES_SEQ;
create sequence DE_OBJECT_ATTR_REC_VALUES_SEQ;
create sequence DYEXTN_ABSTRACT_METADATA_SEQ;
create sequence DYEXTN_ASSO_DISPLAY_ATTR_SEQ;
create sequence DYEXTN_CONTAINER_SEQ;
create sequence DYEXTN_CONTROL_SEQ;
create sequence DYEXTN_DATABASE_PROPERTIES_SEQ;
create sequence DYEXTN_DATA_ELEMENT_SEQ;
create sequence DYEXTN_DE_AUDIT_SEQ;
create sequence DYEXTN_ENTITY_MAP_CONDN_SEQ;
create sequence DYEXTN_ENTITY_MAP_SEQ;
create sequence DYEXTN_ENTITY_RECORD_SEQ;
create sequence DYEXTN_FILE_EXTN_SEQ;
create sequence DYEXTN_FORMULA_SEQ;
create sequence DYEXTN_FORM_CONTEXT_SEQ;
create sequence DYEXTN_FRM_CONTROL_SEQ;
create sequence DYEXTN_PATH_ASSO_REL_SEQ;
create sequence DYEXTN_PATH_SEQ;
create sequence DYEXTN_PERMISSIBLEVAL_SEQ;
create sequence DYEXTN_ROLE_SEQ;
create sequence DYEXTN_RULE_PARAMETER_SEQ;
create sequence DYEXTN_RULE_SEQ;
create sequence DYEXTN_SEMANTIC_PROPERTY_SEQ;
create sequence DYEXTN_TAGGED_VALUE_SEQ;
create sequence DYEXTN_VALUE_DOMAIN_SEQ;
create sequence DYEXTN_VIEW_SEQ;
create sequence DYEX_ATTR_TYP_INFO_SEQ;
