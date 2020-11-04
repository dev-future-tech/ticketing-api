package com.cloudyengineering.ticketing;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WiremockActions implements QuarkusTestResourceLifecycleManager {

    private final Logger log = LoggerFactory.getLogger(WiremockActions.class);

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

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
