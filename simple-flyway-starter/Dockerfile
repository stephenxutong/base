FROM docker-lh.fpi-inc.site/fpi/maven:3.5.4-local AS BUILD
LABEL maintainer="周春松<chunsong_zhou@fpi-inc.com>"
WORKDIR /build/
COPY src/ /build/src/ 
COPY pom.xml /build/
RUN mvn install -DskipTests

FROM docker-lh.fpi-inc.site/fpi/tomcat:8.5.24-slim
COPY --from=BUILD /build/target/*.war /usr/local/tomcat/webapps/ROOT.war