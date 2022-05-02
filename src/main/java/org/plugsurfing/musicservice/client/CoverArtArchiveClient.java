package org.plugsurfing.musicservice.client;

import static java.util.Comparator.comparingLong;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.plugsurfing.musicservice.client.dto.CoverArtArchiveDto;
import org.plugsurfing.musicservice.client.dto.CoverArtArchiveDto.CoverArtImageDto;
import org.plugsurfing.musicservice.dto.AlbumDto;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
public class CoverArtArchiveClient {

    private WebClient client;

    @PostConstruct
    public void init() {
        this.client = WebClient.builder().baseUrl("http://coverartarchive.org/release-group/").//
                clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true))).build();
    }

    public Mono<AlbumDto> getArtistInformationByIdFromMusicBainz(final UUID id, final String title) {

        final Mono<CoverArtArchiveDto> coverArtArchiveMono = this.client.get().uri(id.toString())//
                .accept(MediaType.APPLICATION_JSON) //
                .retrieve()//
                .bodyToMono(CoverArtArchiveDto.class);

        final Mono<CoverArtImageDto> coverArtImageMono = coverArtArchiveMono.map(dto -> dto.getImages() //
                .stream().sorted(comparingLong(CoverArtImageDto::getEdit))//
                .findFirst().get());

        return coverArtImageMono.map(dto -> {
            final AlbumDto albumDto = new AlbumDto(id, title, dto.getImage());
            return albumDto;
        });
    }
}
