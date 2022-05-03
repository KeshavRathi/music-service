package org.plugsurfing.musicservice.client;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.plugsurfing.musicservice.client.dto.ArtistMusicBainzInfoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class MusicBainzClient {

    private WebClient client;

    @PostConstruct
    public void init() {
        this.client = WebClient.builder().baseUrl("http://musicbrainz.org/ws/2").build();
    }

    public Mono<ArtistMusicBainzInfoDto> getArtistInformationByIdFromMusicBainz(final UUID mbId) {

        final String uri = "/artist/" + mbId + "?&fmt=json&inc=url-rels+release-groups"; // URI Builder can also be
                                                                                         // used
        return this.client.get().uri(uri)//
                .accept(MediaType.APPLICATION_JSON)//
                .retrieve()//
                .onStatus(HttpStatus::is4xxClientError,
                        response -> Mono.error(new RuntimeException("Artist not found, Please share a valid ID"))) // Can
                                                                                                                   // be
                                                                                                                   // replaced
                                                                                                                   // by
                                                                                                                   // custom
                                                                                                                   // exception
                .bodyToMono(ArtistMusicBainzInfoDto.class);
    }
}
