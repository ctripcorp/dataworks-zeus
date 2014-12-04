#!/bin/sh
echo "The script can be run only by zeus administator."
set -e
#######################configuration############
#the location of the script which control the tomcat which contains zeus.
ZEUS_TOMCAT=/etc/init.d/tomcat

#the directory of war file of zeus
ZEUS_LOCATION=/opt/ctrip/web/work
#the directory for backing up the old war file of zeus
BACKUP_DIR=${ZEUS_LOCATION}/backup

#git repository of zeus
GIT_REPO=git@git.dev.sh.ctripcorp.com:cdata/prometheus.git
PROJECT_NAME=prometheus

#zeus configuration file
FILE_CONF=/home/kylong/antx.properties

#set MAVEN_HOME
export M2_HOME=/home/kylong/apache-maven-3.2.3
export PATH=${PATH}:${M2_HOME}/bin


###################before_check#################################
curpath=$(cd "$(dirname "$0")"; pwd)
echo "Current workspace of the script is ${curpath}"
##check whether the configuration file exists
if [ ! -e "$FILE_CONF" ]
then 
  echo "The configuration file:antx.properties is not exited"
  exit 1
fi

#check whether set zeus location
if [ -z $ZEUS_LOCATION ]
then
  echo "Please set the parameter ZEUS_LOCATION"
  exit 1
fi

#check whether the backup directory exists
if [ ! -e "${BACKUP_DIR}" ]
then
  mkdir -p $BACKUP_DIR
  if [ $? -eq 0 ]
  then
    echo "Build up the ${BACKUP_DIR} successfully!"
  fi
fi


#check the git
if ! which git > /dev/null
then
  echo "Please install git"
  exit 1
fi

#check mvn
if ! which mvn > /dev/null
then
  echo "Please install mvn"
  exit 1
fi

#check the setting file of mvn
if [ ! -e "~/.m2/settings.xml" ]
then
  echo "Please copy the setting file"
  exit 1
fi

#check whether the project exist
if [ -e "${curpath}/${PROJECT_NAME}" ]
then
  echo "Please delete the zeus project: ${curpath}/${PROJECT_NAME}"
  exit 1
fi

###################install#############################################
tomcat_pid() {
  echo `ps aux | grep -i tomcat|grep org.apache.catalina.startup.Bootstrap | grep -v grep | awk '{ print $2 }'`
}


#stop the tomcat
$ZEUS_TOMCAT stop
pid=$(tomcat_pid)
if [ -n "$pid" ]
then
  echo "tomcat (pid $pid) is running, please check and try again"
  exit 1
fi

#Back up the war file
#check whether the old war file exists and backup
datestr=$(date +%Y%m%d)
if [ -e "$ZEUS_LOCATION/zeus-web.war" ]
then
  mv "${ZEUS_LOCATION}/zeus-web.war" "${BACKUP_DIR}/zeus-web.war.${datestr}"
  echo "Back up successfully!"
fi

if [ -e "${ZEUS_LOCATION}/zeus-web" ]
then
  mv "${ZEUS_LOCATION}/zeus-web" ${curpath}
fi

#git clone from the repository
echo "Begin to git clone from repo"
git clone -v $GIT_REPO
if [ $? -eq 0 ]
then
  echo "Git clone successfully"
else
  exit 1
fi

#replace the configuration
dis_dir="${curpath}/${PROJECT_NAME}/web/src/main/resources"
echo "Copy the configuation ${FILE_CONF} to ${dis_dir}"
cp $FILE_CONF $dis_dir

#begin intall
cd $PROJECT_NAME
#git checkout -b shell origin/shell
mvn install
if [ $? -eq 0 ]
then
  echo "mvn success"
else
  echo "mvn fail"
  exit 1
fi


##############################deploy######################################
target="${curpath}/${PROJECT_NAME}/web/target/zeus-web.war"
echo "Begin to deploy the ${target}"
if [ -n $ZEUS_LOCATION ]
then
  cp $target ${ZEUS_LOCATION}
else
  exit 1
fi

#start tomcat
${ZEUS_TOMCAT} start
echo "Install successfully"
