#!/bin/bash
set -e

cat /dev/null > $CATALINA_HOME/conf/ROOT/database.conf
echo "Edit $CATALINA_HOME/conf/ROOT/database.conf"
{
    echo "db.url=jdbc:postgresql://$CMDBUILD_DB_HOST:$CMDBUILD_DB_PORT/$CMDBUILD_DB_NAME"
    echo "db.username=$CMDBUILD_DB_USER"
    echo "db.password=$CMDBUILD_DB_PASSWORD"
    echo "db.admin.username=$CMDBUILD_DB_USER"
    echo "db.admin.password=$CMDBUILD_DB_PASSWORD"
} >> $CATALINA_HOME/conf/ROOT/database.conf

# first init DB, second start with fail
while ! timeout 1 bash -c "echo > /dev/tcp/$CMDBUILD_DB_HOST/$CMDBUILD_DB_PORT"; do
  >&2 echo "Postgres is unavailable - sleeping"
  sleep 5
done

echo "Init DB"
{ # try

    $CATALINA_HOME/webapps/ROOT/cmdbuild.sh dbconfig create $CMDBUILD_DB_DUMP -configfile $CATALINA_HOME/conf/ROOT/database.conf

} || {
    echo "DB was initiliazed. Use dbconfig recreate or dbconfig drop"
}

#echo "Change user to tomcat"
#su tomcat

sed -i -E "s|@@FQDN@@|${TOMCAT_PROXY_FQDN}|g" ${CATALINA_HOME}/conf/server.xml

#echo "RUN catalina"
exec $CATALINA_HOME/bin/catalina.sh run