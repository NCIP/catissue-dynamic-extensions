//This is to set the max length of field to 4000
update dyextn_string_type_info set max_size = 4000 where max_size = 255;

update DYEXTN_ROLE set ASSOCIATION_TYPE='CONTAINTMENT' where IDENTIFIER in (
select TARGET_ROLE_ID from DYEXTN_ASSOCIATION where IS_COLLECTION =1);

update DYEXTN_ROLE set ASSOCIATION_TYPE='CONTAINTMENT' where IDENTIFIER in (
select SOURCE_ROLE_ID from DYEXTN_ASSOCIATION where IS_COLLECTION =1);

commit;
