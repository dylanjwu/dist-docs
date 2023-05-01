cd ./FileStorage
./start_docker.sh
cd ..
docker compose down --remove-orphans
docker compose up --build


