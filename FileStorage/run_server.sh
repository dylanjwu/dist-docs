PROJECT_NETWORK='final_project-network'

SERVER_IMAGE='final_project-server-image'
#SERVER_CONTAINER_1='my-server-1'
#SERVER_CONTAINER_2='my-server-2'
#SERVER_CONTAINER_3='my-server-3'
#SERVER_CONTAINER_4='my-server-4'
#SERVER_CONTAINER_5='my-server-5'
#
#
#COORDINATOR_IMAGE='final_project-network-coordinator-image'
COORDINATOR_CONTAINER='my-coordinator'

# shellcheck disable=SC2034
BUCKET_NAME_1='6650-replication-1'

BUCKET_NAME_2='6650-replication-2'

BUCKET_NAME_3='6650-replication-3'

BUCKET_NAME_4='6650-replication-4'

BUCKET_NAME_5='6650-replication-5'



JAR_NAME='server-1.0.0.jar'

if [ $# -ne 6 ]
then
  echo "Usage: ./run_server.sh <port-number 1> <port-number 2> <port-number 3> <port-number 4> <port-number 5>
  <coordinator port-number 6> "
  exit
fi

docker run -d --name "my-server-1" \
 --network $PROJECT_NETWORK $SERVER_IMAGE \
 java -jar $JAR_NAME "$1" $COORDINATOR_CONTAINER "$6" "$BUCKET_NAME_1"


docker run -d -p "$2":"$2" --name "my-server-2" \
 --network $PROJECT_NETWORK $SERVER_IMAGE \
 java -jar $JAR_NAME "$2" $COORDINATOR_CONTAINER "$6" "$BUCKET_NAME_2"


docker run -d -p "$3":"$3" --name "my-server-3" \
 --network $PROJECT_NETWORK $SERVER_IMAGE \
 java -jar $JAR_NAME "$3" $COORDINATOR_CONTAINER "$6" "$BUCKET_NAME_3"

docker run -d -p "$4":"$4" --name "my-server-4" \
 --network $PROJECT_NETWORK $SERVER_IMAGE \
 java -jar $JAR_NAME "$4" $COORDINATOR_CONTAINER "$6" "$BUCKET_NAME_4"

docker run -d -p "$5":"$5" --name "my-server-5" \
 --network $PROJECT_NETWORK $SERVER_IMAGE \
 java -jar $JAR_NAME "$5" $COORDINATOR_CONTAINER "$6" "$BUCKET_NAME_5"

