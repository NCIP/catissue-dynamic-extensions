ALTER TABLE DYEXTN_FORMULA ALTER column expression SET DATA TYPE varchar(4000);

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
