FROM liferay/liferay-de:20170703073124007430605-db
MAINTAINER Sergio González <sergio.gonzalez@liferay.com>

COPY deploy/*.?ar $LIFERAY_HOME/osgi/modules/
COPY supervisord-tomcat.conf /etc/supervisor/conf.d/supervisord-tomcat.conf
COPY portal-ext.properties /liferay/portal-ext.properties