package org.plugsurfing.musicservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.plugsurfing.musicservice.client.CoverArtArchiveClient;
import org.plugsurfing.musicservice.client.MusicBainzClient;
import org.plugsurfing.musicservice.client.WikiDataClient;
import org.plugsurfing.musicservice.client.WikipediaClient;
import org.plugsurfing.musicservice.client.dto.ArtistMusicBainzInfoDto;
import org.plugsurfing.musicservice.client.dto.ArtistMusicBainzInfoDto.ReleaseGroupsDto;
import org.plugsurfing.musicservice.client.dto.WikipediaInfoDto;
import org.plugsurfing.musicservice.dto.AlbumDto;
import org.plugsurfing.musicservice.dto.ArtistInformationDto;
import org.plugsurfing.musicservice.service.ArtistInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ArtistInformationServiceImpl implements ArtistInformationService {

    private static final Logger logger = LogManager.getLogger(ArtistInformationServiceImpl.class);

    @Autowired
    private MusicBainzClient mbClient;

    @Autowired
    private WikiDataClient wikiDataClient;

    @Autowired
    private WikipediaClient wikipediaClient;

    @Autowired
    private CoverArtArchiveClient coverArtArchiveClient;

    @Override
    public Mono<ArtistInformationDto> getArtistInformation(final UUID mbId) {

        logger.info("Received request for Artist with MbId : {}", mbId);

        final Mono<ArtistMusicBainzInfoDto> artistMBInfo = this.mbClient.getArtistInformationByIdFromMusicBainz(mbId);

        final Mono<Pair<ArtistMusicBainzInfoDto, String>> wikiDataInformationMono = this.fetchWikiData(artistMBInfo);

        final Mono<Pair<ArtistMusicBainzInfoDto, WikipediaInfoDto>> wikipediaDataMono = this
                .fetchWikipediaInfo(wikiDataInformationMono);

        final Mono<List<AlbumDto>> listOfAlbumDtoMono = this.fetchCoverArtInformation(artistMBInfo);

        return this.convertToArtistInformationDto(mbId, wikipediaDataMono, listOfAlbumDtoMono);

    }

    private Mono<ArtistInformationDto> convertToArtistInformationDto(final UUID mbId,
            final Mono<Pair<ArtistMusicBainzInfoDto, WikipediaInfoDto>> wikipediaDataMono,
            final Mono<List<AlbumDto>> listOfAlbumDtoMono) {
        return Mono.zip(wikipediaDataMono, listOfAlbumDtoMono, (tuple, albums) -> {

            final WikipediaInfoDto wikipediaData = tuple.getRight();
            final String wikipediaDescription = wikipediaData.getExtract();

            final ArtistMusicBainzInfoDto artistMusicBainzInfoDto = tuple.getLeft();
            final ArtistInformationDto dto = ArtistInformationDto.builder().mbid(mbId) //
                    .name(artistMusicBainzInfoDto.getName()) //
                    .description(wikipediaDescription) //
                    .country(artistMusicBainzInfoDto.getCountry())//
                    .disambiguation(artistMusicBainzInfoDto.getDisambiguation()) //
                    .gender(artistMusicBainzInfoDto.getGender()) //
                    .albums(albums).build();

            return dto;
        });
    }

    private Mono<List<AlbumDto>> fetchCoverArtInformation(final Mono<ArtistMusicBainzInfoDto> artistMBInfo) {
        final Flux<ReleaseGroupsDto> releaseGroupsFlux = artistMBInfo
                .flatMapIterable(value -> value.getReleaseGroups());

        final Mono<List<AlbumDto>> listOfAlbumDtoMono = releaseGroupsFlux
                .flatMap(s -> this.coverArtArchiveClient.getCoverArtInformation(s.getId(), s.getTitle())).collectList();
        return listOfAlbumDtoMono;
    }

    private Mono<Pair<ArtistMusicBainzInfoDto, WikipediaInfoDto>> fetchWikipediaInfo(
            final Mono<Pair<ArtistMusicBainzInfoDto, String>> wikiDataInformationMono) {
        return wikiDataInformationMono.zipWhen(tuple -> {
            final String wikiDataString = tuple.getRight();
            final JSONObject wikiDataJson = new JSONObject(wikiDataString);
            return this.wikipediaClient.getWikipediaInformation(this.getWikipediaTitle(wikiDataJson));
        }, (mbInfo, b) -> new ImmutablePair<ArtistMusicBainzInfoDto, WikipediaInfoDto>(mbInfo.getLeft(), b));
    }

    private Mono<Pair<ArtistMusicBainzInfoDto, String>> fetchWikiData(
            final Mono<ArtistMusicBainzInfoDto> artistMBInfo) {
        return artistMBInfo.zipWhen(mbInfo -> {
            final Optional<String> wikidataFromArtistInfo = mbInfo.getRelations().stream()
                    .filter(relation -> relation.getType().equals("wikidata")) //
                    .map(relation -> {
                        final String url = relation.getUrl().getResource();
                        final int lastIndexOf = url.lastIndexOf("/");
                        return url.substring(lastIndexOf + 1);
                    }).findFirst();
            logger.debug("WikiData id: {}", wikidataFromArtistInfo.get());
            return this.wikiDataClient.getWikidataForId(wikidataFromArtistInfo.get());
        }, (mbInfo, b) -> new ImmutablePair<ArtistMusicBainzInfoDto, String>(mbInfo, b));
    }

    private String getWikipediaTitle(final JSONObject wikiDataJson) {

        final String key = wikiDataJson.getJSONObject("entities").keys().next();
        return wikiDataJson.getJSONObject("entities").getJSONObject(key).getJSONObject("sitelinks")
                .getJSONObject("enwiki").getString("title").replace(" ", "_");
    }
}
