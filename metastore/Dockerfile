FROM azul/zulu-openjdk:17.0.4.1

RUN apt update

RUN apt-get update && apt-get upgrade -y \

 && apt-get install -y wget

RUN wget "https://repo1.maven.org/maven2/org/apache/hive/hive-standalone-metastore/3.1.3/hive-standalone-metastore-3.1.3-bin.tar.gz"

RUN tar -zxvf hive-standalone-metastore-3.1.3-bin.tar.gz

RUN mv apache-hive-metastore-3.1.3-bin /usr/local/metastore

RUN wget "https://downloads.apache.org/hadoop/common/hadoop-3.3.4/hadoop-3.3.4.tar.gz"

RUN tar xvf hadoop-3.3.4.tar.gz

RUN mv hadoop-3.3.4 /usr/local/hadoop

RUN rm /usr/local/metastore/lib/guava-19.0.jar

RUN cp /usr/local/hadoop/share/hadoop/common/lib/guava-27.0-jre.jar /usr/local/metastore/lib/

RUN cp /usr/local/hadoop/share/hadoop/tools/lib/hadoop-aws-3.3.4.jar /usr/local/metastore/lib/

RUN cp /usr/local/hadoop/share/hadoop/tools/lib/aws-java-sdk-bundle-1.12.262.jar /usr/local/metastore/lib/

RUN wget https://jdbc.postgresql.org/download/postgresql-42.5.0.jar

RUN mv postgresql-42.5.0.jar /usr/local/metastore/lib/

ENV HADOOP_HOME=/usr/local/hadoop

RUN rm hive-standalone-metastore-3.1.3-bin.tar.gz hadoop-3.3.4.tar.gz

CMD /usr/local/metastore/bin/schematool -initSchema -dbType postgres ; /usr/local/metastore/bin/start-metastore
