How To run the System:

1. run docker build . 
2. run each container in the following way: docker run --net host CITY_NAME GRPC_PORT ZOOKEEPER_ADDRESS
    example: docker run --net host TLV 23000 127.0.0.1:2181
3. run client tests: python3.9 stress_tests.py