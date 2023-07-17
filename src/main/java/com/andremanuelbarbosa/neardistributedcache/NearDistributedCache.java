package com.andremanuelbarbosa.neardistributedcache;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class NearDistributedCache {

    public NearDistributedCache(int port) {

        try {

            final HttpServer httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);

            httpServer.start();

            Hazelcast.newHazelcastInstance();

            final HazelcastInstance hazelcastInstanceClient = HazelcastClient.newHazelcastClient();

            final Map<String, String> cache = hazelcastInstanceClient.getMap("cache");

            if (cache.containsKey("string")) {

                System.out.println("Fetching record from Cache");

            } else {

                cache.put("string", "string-value");

                System.out.println("Storing record in Cache");
            }

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        new NearDistributedCache(8001);
        new NearDistributedCache(8002);
        new NearDistributedCache(8003);
    }
}
