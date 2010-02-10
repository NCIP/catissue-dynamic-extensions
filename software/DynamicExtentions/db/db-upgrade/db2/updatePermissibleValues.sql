drop procedure replaceString#
drop procedure updatePV#

create PROCEDURE replaceString (IN colName VARCHAR(200),IN tabName VARCHAR(200), IN oldString VARCHAR(200), IN newString VARCHAR(200) )
		BEGIN
		DECLARE sqlQuery varchar(300);
			set sqlQuery = 'update '|| tabName ||' set '||colName ||' = REPLACE ( '||colName||' , '''|| oldString || ''' , '''||newString||''' ) ';
			EXECUTE IMMEDIATE sqlQuery;
			commit;

		END#

create procedure updatePV()
BEGIN
DECLARE SQLSTATE  CHAR(5) DEFAULT '00000';
DECLARE attributeId INTEGER;
DECLARE columnName varchar(200);
DECLARE tableName varchar(200);

DECLARE	 cursorQuery CURSOR FOR select PRIMITIVE_ATTRIBUTE_ID from DYEXTN_ATTRIBUTE_TYPE_INFO where IDENTIFIER in
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

		OPEN cursorQuery;


		FETCH cursorQuery INTO attributeId;
		while(SQLSTATE= '00000') DO
		select NAME into columnName from DYEXTN_DATABASE_PROPERTIES where identifier in(select IDENTIFIER from DYEXTN_COLUMN_PROPERTIES where PRIMITIVE_ATTRIBUTE_ID = attributeId);
		select NAME into tableName from DYEXTN_DATABASE_PROPERTIES where identifier in(select IDENTIFIER from DYEXTN_TABLE_PROPERTIES where ABSTRACT_ENTITY_ID in (select ENTIY_ID from DYEXTN_ATTRIBUTE  where IDENTIFIER = attributeId));
		call replaceString(columnName,tableName,'<=','Less Than Or Equal');
		call replaceString(columnName,tableName,'>=','Greater Than Or Equal');
		call replaceString(columnName,tableName,'<','Less Than');
		call replaceString(columnName,tableName,'>','Greater Than');
		FETCH cursorQuery INTO attributeId;
		END WHILE;
		call replaceString('value','DYEXTN_STRING_CONCEPT_VALUE','<=','Less Than Or Equal');
		call replaceString('value','DYEXTN_STRING_CONCEPT_VALUE','>=','Greater Than Or Equal');
		call replaceString('value','DYEXTN_STRING_CONCEPT_VALUE','<','Less Than');
		call replaceString('value','DYEXTN_STRING_CONCEPT_VALUE','>','Greater Than');
	 CLOSE cursorQuery;

	commit;

   END#

   call updatePV()#
