# solr base rep version
FROM registry.zhaopin.com/zhaopin/solr-app:8.6.3
MAINTAINER liyw "li.yanwei@zhaopin.com.cn"
ENV REFRESHED_AT 2016-6-6
#ENV
ENV TZ='Asia/Shanghai'
ENV JAVA_OPTS "-Duser.timezone=GMT+08"

ADD src/main/resources/web.xml /opt/solr/server/solr-webapp/webapp/WEB-INF
ADD build/libs/solr-components-1.0.0.RELEASE.jar /opt/solr/server/solr-webapp/webapp/WEB-INF/lib

