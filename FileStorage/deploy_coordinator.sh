PROJECT_NETWORK='final_project-network'

SERVER_IMAGE='final_project-server-image'
SERVER_CONTAINER_1='my-server-1'
SERVER_CONTAINER_2='my-server-2'
SERVER_CONTAINER_3='my-server-3'
SERVER_CONTAINER_4='my-server-4'
SERVER_CONTAINER_5='my-server-5'


COORDINATOR_IMAGE='final_project-network-coordinator-image'
COORDINATOR_CONTAINER='my-coordinator'


# clean up existing resources, if any
echo "----------Cleaning up existing resources----------"
docker container stop $SERVER_CONTAINER_1 2> /dev/null && docker container rm $SERVER_CONTAINER_1 2> /dev/null
docker container stop $SERVER_CONTAINER_2 2> /dev/null && docker container rm $SERVER_CONTAINER_2 2> /dev/null
docker container stop $SERVER_CONTAINER_3 2> /dev/null && docker container rm $SERVER_CONTAINER_3 2> /dev/null
docker container stop $SERVER_CONTAINER_4 2> /dev/null && docker container rm $SERVER_CONTAINER_4 2> /dev/null
docker container stop $SERVER_CONTAINER_5 2> /dev/null && docker container rm $SERVER_CONTAINER_5 2> /dev/null
docker container stop $COORDINATOR_CONTAINER 2> /dev/null && docker container rm $COORDINATOR_CONTAINER 2> /dev/null
docker network rm $PROJECT_NETWORK 2> /dev/null

# only cleanup
if [ "$1" == "cleanup-only" ]
then
  exit
fi

# create a custom virtual network
echo "----------creating a virtual network----------"
docker network create $PROJECT_NETWORK

# build the images from Dockerfile
echo "----------Building images----------"
docker build -t $SERVER_IMAGE -f Dockerfile_server .
docker build -t $COORDINATOR_IMAGE -f Dockerfile_coordinator .


if [ $# -ne 1 ]
then
  echo "Usage: ./deploy.sh <port-number>"
  exit
fi

# run the image and open the required ports
echo "----------Running coordinator app----------"
docker run -d -p "$1":"$1" --name $COORDINATOR_CONTAINER --network $PROJECT_NETWORK $COORDINATOR_IMAGE \
java -jar 'coordinator-1.0.0.jar' "$1"

# echo "----------watching logs from coordinator----------"
# docker logs $COORDINATOR_CONTAINER -f
