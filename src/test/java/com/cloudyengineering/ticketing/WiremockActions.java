package com.cloudyengineering.ticketing;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WiremockActions implements QuarkusTestResourceLifecycleManager {

    private final Logger log = LoggerFactory.getLogger(WiremockActions.class);

    @Override
    public Map<String, String> start() {
        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8090));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8090);

        stubFor(post(urlPathEqualTo("/api/action")).withQueryParam("action_name", equalTo("Created a new assignee with id 1"))
            .willReturn(aResponse()
                .withHeader("Location", "http://localhost:8081/api/assignee/1")
                    .withHeader("Content-Type", "application/json")
                    .withStatus(201)
            )
        );

        stubFor(get(urlPathEqualTo("/metrics"))
                .willReturn(aResponse().withStatus(200))
        );

        log.debug("Stub URL is {}", wireMockServer.baseUrl());

        return Collections.singletonMap("actions-api/mp-rest/url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {

    }
}
