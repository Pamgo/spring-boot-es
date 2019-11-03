package com.okay.conf;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sun.rmi.transport.Transport;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class MyConf {

    @Bean
    public TransportClient client() throws UnknownHostException {

        // 这里使用的tcp端口为9300，默认
        InetSocketTransportAddress  node = new InetSocketTransportAddress(
                InetAddress.getByName("localhost"),
                9300
        );

        Settings settings = Settings.builder()
                .put("cluster.name","byterun-es")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(node);

        return client;
    }

}
