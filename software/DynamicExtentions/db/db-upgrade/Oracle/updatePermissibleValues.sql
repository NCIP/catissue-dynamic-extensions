DECLARE
		CURSOR cursorQuery IS select PRIMITIVE_ATTRIBUTE_ID from DYEXTN_ATTRIBUTE_TYPE_INFO where IDENTIFIER in
				(
					select ATTRIBUTE_TYPE_INFO_ID from DYEXTN_DATA_ELEMENT where IDENTIFIER in
					(
						(
							select USER_DEF_DE_ID from DYEXTN_USERDEF_DE_VALUE_REL where PERMISSIBLE_VALUE_ID in
							(
								SELECT identifier from DYEXTN_STRING_CONCEPT_VALUE where value like '%<%' or  value like '%>%' or value like '%<=%' or value like '%>=%'
							)
						)
					)
				  union
					select ATTRIBUTE_TYPE_INFO_ID from dyextn_permissible_value where identifier in
					(
						(
							SELECT identifier from DYEXTN_STRING_CONCEPT_VALUE where value like '%<%' or  value like '%>%' or value like '%<=%' or value like '%>=%'
						)
					 )
				  union
					select identifier from DYEXTN_ATTRIBUTE_TYPE_INFO where PRIMITIVE_ATTRIBUTE_ID in
					(
						select ABSTRACT_ATTRIBUTE_ID from dyextn_category_attribute where IDENTIFIER in
						(
							select CATEGORY_ATTRIBUTE_ID from dyextn_permissible_value where identifier in
							(
								SELECT identifier from DYEXTN_STRING_CONCEPT_VALUE where value like '%<%' or  value like '%>%' or value like '%<=%' or value like '%>=%'
							)and CATEGORY_ATTRIBUTE_ID is not null
						)
					)

				)
			  union
				select primitiveAttribute.IDENTIFIER from dyextn_primitive_attribute primitiveAttribute,dyextn_attribute attribute
				where primitiveAttribute.IDENTIFIER = attribute.IDENTIFIER and attribute.ENTIY_ID in
				(
					select TARGET_ENTITY_ID from dyextn_association where IDENTIFIER in
					(
						select ABSTRACT_ATTRIBUTE_ID from dyextn_category_attribute where IDENTIFIER in
						(
							select CATEGORY_ATTRIBUTE_ID from dyextn_permissible_value where identifier in
							(
								SELECT identifier from DYEXTN_STRING_CONCEPT_VALUE where value like '%<%' or  value like '%>%' or value like '%<=%' or value like '%>=%'
							)
						)
					)
				)and primitiveAttribute.IS_PRIMARY_KEY = 0;

		attributeId DYEXTN_ATTRIBUTE_TYPE_INFO.PRIMITIVE_ATTRIBUTE_ID%Type;
		columnName varchar2(200);
		tableName varchar2(200);
		sql_stmt varchar2(300);
		PROCEDURE replaceString (colName IN VARCHAR2,tabName IN VARCHAR2, oldString IN VARCHAR2, newString in VARCHAR2 ,sqlQuery out VARCHAR2) AS
		BEGIN
			sqlQuery :='update '|| tabName ||' set '||colName ||' = REPLACE ( '||colName||' , '''|| oldString || ''' , '''||newString||''' ) ';
			execute immediate sqlQuery;
			commit;

		END;

	BEGIN
      OPEN cursorQuery;

     FETCH cursorQuery INTO attributeId;
      WHILE (cursorQuery%FOUND) LOOP
		select NAME into columnName from DYEXTN_DATABASE_PROPERTIES where identifier in(select IDENTIFIER from DYEXTN_COLUMN_PROPERTIES where PRIMITIVE_ATTRIBUTE_ID = attributeId);
		select NAME into tableName from DYEXTN_DATABASE_PROPERTIES where identifier in(select IDENTIFIER from DYEXTN_TABLE_PROPERTIES where ABSTRACT_ENTITY_ID in (select ENTIY_ID from DYEXTN_ATTRIBUTE  where IDENTIFIER = attributeId));
		replaceString(columnName,tableName,'<=','Less Than Or Equal',sql_stmt);
		replaceString(columnName,tableName,'>=','Greater Than Or Equal',sql_stmt);
		replaceString(columnName,tableName,'<','Less Than',sql_stmt);
		replaceString(columnName,tableName,'>','Greater Than',sql_stmt);
		FETCH cursorQuery INTO attributeId;
     END LOOP;
		replaceString('value','DYEXTN_STRING_CONCEPT_VALUE','<=','Less Than Or Equal',sql_stmt);
		replaceString('value','DYEXTN_STRING_CONCEPT_VALUE','>=','Greater Than Or Equal',sql_stmt);
		replaceString('value','DYEXTN_STRING_CONCEPT_VALUE','<','Less Than',sql_stmt);
		replaceString('value','DYEXTN_STRING_CONCEPT_VALUE','>','Greater Than',sql_stmt);
     CLOSE cursorQuery;
	 commit;
   END;

