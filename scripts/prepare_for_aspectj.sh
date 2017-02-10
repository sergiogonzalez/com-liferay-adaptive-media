#! /bin/sh

set -e

ls ../bundles/tomcat-8.0.32/webapps/ROOT/WEB-INF/lib/

rm ../bundles/tomcat-8.0.32/webapps/ROOT/WEB-INF/lib/*aspectj*

echo '################################################################################'

pwd

echo '################################################################################'

cp scripts/com.liferay.aspectj.system.exit.jar ../bundles/tomcat-8.0.32/lib/ext/

ls ../bundles/tomcat-8.0.32/lib/ext/


#cat ../bundles/tomcat-8.0.32/bin/setenv.sh

#echo CATALINA_OPTS=\"\${CATALINA_OPTS} -javaagent:`pwd`/scripts/aspectj-weaver.jar -Dorg.aspectj.weaver.loadtime.configuration=com/liferay/aspectj/aop.xml\" >> ../bundles/tomcat-8.0.32/bin/setenv.sh

#cat ../bundles/tomcat-8.0.32/bin/setenv.sh


