cd ./../temp/project

echo ===============================================================================
echo running for mysql database

copy  "..\..\conf\ApplicationDAOProperties_Mysql.xml" "./src/ApplicationDAOProperties.xml" /Y

copy  "..\..\extra_lib\jta.jar" "./WEB-INF/lib" /Y

copy  "..\..\extra_lib\jboss-j2ee.jar" "./WEB-INF/lib" /Y

call ant db_initialized

call ant generate_schema

call ant generate_codecoverage_report

copy "./query.sql" "..\..\de_release\dynamicExtensions_MySQL.sql" /Y

copy  "..\..\conf\db_oracle.properties" "./db.properties" /Y

call ant generate_schema -Dis_generate_schema="false"

copy  "./query.sql" "..\..\de_release\dynamicExtensions_Oracle.sql" /Y
echo ===============================================================================