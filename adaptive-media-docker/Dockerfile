FROM liferay/liferay-portal:master-c35cf267e06d5a1390adb35cd5c5bcc8fb5ce2c7
MAINTAINER Sergio González <sergio.gonzalez@liferay.com>

COPY deploy/*.?ar $LIFERAY_HOME/osgi/modules/
COPY supervisord-tomcat.conf /etc/supervisor/conf.d/supervisord-tomcat.conf
COPY portal-ext.properties /liferay/portal-ext.properties