
## Dist Doc Project

### Instructions on building and running


There are 4 main components to this project:

Frontend:
- client (React frontend app that connects to notfications server and central server)

Backend:
- postgre (central server Spring boot app which sends rmi requests to FileStorage service and connects with postgres db running on AWS RDS)
- FileStorage (document rmi server replicas connecting to S3 bucket replicas). The JAR files for the server and coordinator (used by docker) are included in the target subfolder
- notifications server (node.js app connecting to a kafka container running with Zookeeper)

#### Backend

To run the entire backend system, run the shelll script: ```./run_whole_program.sh```. This will build and run all server and coordinator docker containers in FileStorage directory, as well as the docker-compose.yaml file in the main directory -- which spins up services: node-app (the notifications server that depends on kafka and zookeeper) and central-server which is built off the postgre directory containing the central server Spring Boot app.

Once all backend services are up and running (which you can monitor with the docker-compose logs in your terminal), you can spin up the client.

Note: it is possible that node-app service will fail when connecting to kafka. If you do not see:

```
node_app        | Topics created successfully
node_app        | consumers created
```

in the logs, then run run_whole_program.sh again. If, for some reason, the node_app does not continue to run, that's fine -- the other components of the app will still work -- users just won't get notifications.

#### Client

To run the client, navigate to client directory, run  ```npm ci``` to install dependencies off the package-lock.json file, and then run ```npm start``` to run the react app on ```localhost:3000```. This app will then connect to the notifications server on local port 3005 and central server on local port 8080.

On the react app, you can go to the login page and enter the user credentials:

```username: sida; password: 123``` and/or ```username: jacky; password: 123```. If testing with multiple users, note that localstorage is modified -- user_id key is set to the user's user id.

You can also sign up with a new account if you'd like.