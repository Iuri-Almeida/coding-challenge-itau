###########################################################
# BUILD - DUMP
###########################################################
FROM postgres:14 as dumper

COPY dump.sql /docker-entrypoint-initdb.d/

#RUN ["sed", "-i", "s/exec \"$@\"/echo \"skipping...\"/", "/usr/local/bin/docker-entrypoint.sh"]

#ENV PG_USER=postgres
#ENV POSTGRES_USER=postgres
#ENV POSTGRES_PASSWORD=postgres
#ENV PGDATA=/data

#RUN ["/usr/local/bin/docker-entrypoint.sh", "postgres"]
#RUN ["pg_restore", "-C", "-d", "postgres", "/docker-entrypoint-initdb.d/", "dump.sql"]
############################################################

############################################################
# BUILD - RESTORE
############################################################
#FROM postgres:14

#COPY --from=dumper /data $PGDATA
############################################################
