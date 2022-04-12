package org.plugsurfing.musicservice.client;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class CoverArtArchiveClient {

    private WebClient client;

    @PostConstruct
    public void init() {
        this.client = WebClient.builder().baseUrl("http://coverartarchive.org/release-group").build();
    }

    public Mono<String> getArtistInformationByIdFromMusicBainz(final UUID id) {

        return this.client.get().uri(id.toString())//
//                .accept(MediaType.APPLICATION_JSON)//
                .retrieve()//
                .bodyToMono(String.class);
    }
}
