from rest_client import server_rest_ports
import os

NUM_SERVERS = 2

start = 25000

# clean up
os.system('sudo docker container kill $(sudo docker ps -q)')
os.system('sudo docker container rm $(sudo docker ps -a -q)')


for city in server_rest_ports:
    for num in range(NUM_SERVERS):
        os.system(
            f'docker run -d --net host  --name {city + str(num)} ds_hw2_4_cities {city} {start} 127.0.0.1:2181; sleep 10')
        start += 1
