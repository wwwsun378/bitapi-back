FROM 172.31.3.230:5000/java:8
VOLUME /tmp
ADD target/ii-backstage.jar ii-backstage.jar
RUN bash -c 'touch /ii-backstage.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Duser.region=CN -Duser.timezone=GMT=GMT+8 -Djava.security.egd=file:/dev/./urandom -jar /ii-backstage.jar"]