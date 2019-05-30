package com.github.spring.esdata.loader.demo;

import org.springframework.boot.test.util.TestPropertyValues;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

public final class DemoTestPropertyValues {

    public static final String ES_DOCKER_IMAGE_VERSION = "docker.elastic.co/elasticsearch/elasticsearch:" +
            System.getProperty("ES-DOCKER-IMAGE-VERSION", "6.7.1");

    private DemoTestPropertyValues() {
    }

    public static TestPropertyValues using(ElasticsearchContainer esContainer) {
        String esTcpHost = esContainer.getTcpHost().getHostName() + ":" + esContainer.getTcpHost().getPort();
        return TestPropertyValues.of("spring.data.elasticsearch.clusterNodes=" + esTcpHost);
    }
}
