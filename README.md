# Introduction
TODO

## The Problem
Consider a simple Service with three instances running behind a Load Balancer which needs to read/write from a DB like the one in diagram below.
In this scenario, each of the nodes needs to interact with the DB to perform the retrievals and updates of data.

![The Problem](./images/NearDistributedCacheProblem.jpeg "The Problem")

This is of course a naive implementation without optimizations but the goal here was just use this architecture as a starting point.

## Measurements
For the purpose of evaluating the different approaches discussed in this article we will use a simple scenario where we attempt to fetch six times the same resource via the service load balancer (assuming a simple round-robin approach) and the following simple metrics:
* \# DB Queries: the number of queries that had to be executed in the DB in order to fetch the record
* \# Network Requests: the number of requests where each node had to use the network to retrieve the information needed

### Evaluation
This is the metrics baseline for simple architecture described above that will also be used as comparison to the other approaches:
* 6 DB Queries: each request had to perform a retrieval operation in the DB to fetch the record
* 6 Network Requests: each request to connect to the DB in order to fetch the record

# Existing Approaches

## Near Cache
One of most common approaches to reduce the number of interactions with the DB is to use a Near Cache framework like for example MemCache.
In this scenario, an instance of MemCache is initialized in each node and the data lifecycle needs to managed in each node.  

![Near Cache](./images/NearDistributedCacheMemCache.jpeg "Near Cache")

### Evaluation
* 3 DB Queries: each node had to load the same record from the DB for the first three requests
* 3 Network Requests: each node had to load the same record from the DB for the first three requests, following three were served from cache

## Distributed Cache
Another common approach to optimize data retrieval applications is to use a distributed cache approach like Redis, which is a lot faster than fetching the data from the DB.
In this scenario, the first node that fetches the record is responsible for storing that data in Redis, which then becomes available for all other nodes.

![Distributed Cache](./images/NearDistributedCacheRedis.jpeg "Distributed Cache")

### Evaluation
* 1 DB Queries: first node that received the request fetched the record from the DB and stored in Redis
* 7 Network Requests: one request to fetch the record from the DB, one more to store that record in Redis and five more requests to fetch the data from Redis on the following requests

## Near + Distributed Cache
A combination of both Near and Distributed Cache is a more complex but effective approach to take advantage from both techniques, where we will leverage the same stack as before, so MemCache as near cache and Redis as distributed cache.
In this scenario, the first node that fetches the record is responsible for storing that data in Redis, which then becomes available for all other nodes, and also storing that record in the local instance of MemCache. When requests reach other nodes, first the data will be retrieved from Redis and stored in MemCache for subsequent requests made on the same node. 

![Near + Distributed Cache](./images/NearDistributedCacheMemCacheAndRedis.jpeg "Near + Distributed Cache")

### Evaluation
* 1 DB Queries: first node that received the request fetched the record from the DB and stored in Redis
* 4 Network Requests: one request to fetch the record from the DB, one more to store that record in Redis and two more requests to fetch the data from Redis on the following requests from the other two nodes, then remaining three requested were served from near cache

# Near-Distributed Cache


![Near-Distributed Cache](./images/NearDistributedCacheHazelcast.jpeg "Near-Distributed Cache")

## Evaluation
* 1 DB Queries: first node that received the request fetched the record from the DB and stored in Hazelcast near cache
* 1+2 Network Requests: first node connected to the DB to fetch the record, plus two requests in the background for Hazelcast to propagate the cache to the other two nodes
