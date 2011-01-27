DECLARE
    dataElementId bigint(20);
    permValId bigint(20);
    catAttrId bigint(20);
    udeId bigint(20);
	CURSOR pvQuery FOR select identifier,CATEGORY_ATTRIBUTE_ID from
		DYEXTN_DATA_ELEMENT where CATEGORY_ATTRIBUTE_ID is not null;

BEGIN
      OPEN pvQuery;
      LOOP
		FETCH pvQuery INTO dataElementId,catAttrId;
			BEGIN
				select identifier into permValId from DYEXTN_PERMISSIBLE_VALUE
				where CATEGORY_ATTRIBUTE_ID = catAttrId;

				insert into DYEXTN_USERDEF_DEF_PV_REL(USER_DEF_DE_ID, PERMISSIBLE_VALUE_ID)
				values(dataElementId, permValId);

				update DYEXTN_PERMISSIBLE_VALUE set CATEGORY_ATTRIBUTE_ID=null
				where CATEGORY_ATTRIBUTE_ID = catAttrId;

			EXCEPTION
				when NO_DATA_FOUND THEN
					permValId := 0;
				when DUP_VAL_ON_INDEX THEN
					permValId := 0;
			END;
		EXIT WHEN pvQuery%NOTFOUND;

      END LOOP;

      CLOSE pvQuery;
      commit;
END;


