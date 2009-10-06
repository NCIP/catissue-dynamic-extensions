cd ../temp/project/software/DynamicExtentions

echo ===============================================================================
echo running for mysql database

copy  "..\..\..\..\src\conf\ApplicationDAOProperties_Mysql.xml" "./src/java/ApplicationDAOProperties.xml" /Y

copy  "..\..\..\..\lib\extra\jta.jar" "./lib/server" /Y

copy  "..\..\..\..\lib\extra\jboss-j2ee.jar" "./lib/server" /Y

call ant db_initialized

call ant generate_schema

call ant generate_codecoverage_report

copy "./query.sql" "..\..\..\..\binaries\de_release\dynamicExtensions_MySQL.sql" /Y

copy  "..\..\..\..\src\conf\db_oracle.properties" "./src/conf/db.properties" /Y

call ant generate_schema -Dis_generate_schema="false"

copy  "./query.sql" "..\..\..\..\binaries\de_release\dynamicExtensions_Oracle.sql" /Y
echo ===============================================================================