package org.plugsurfing.musicservice.client;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

@Component
public class CoverArtArchiveClient {

    private WebClient client;

    @PostConstruct
    public void init() {
        this.client = WebClient.builder().baseUrl("http://coverartarchive.org/release-group/").//
                clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true))).build();
    }

    public Flux<String> getArtistCoverInformation(final UUID id) {

        return this.client.get().uri(id.toString())//
                .retrieve()//
                .bodyToFlux(String.class);
    }
}
