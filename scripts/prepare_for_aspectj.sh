#! /bin/sh

set -e

ls ../bundles/tomcat-8.0.32/webapps/ROOT/WEB-INF/lib/

rm ../bundles/tomcat-8.0.32/webapps/ROOT/WEB-INF/lib/*aspectj*

pwd

echo 'HOLA'

cp ./scripts/com.liferay.aspectj.system.exit.jar ../bundles/tomcat-8.0.32/lib/ext

ls ../bundles/tomcat-8.0.32/lib/ext/

