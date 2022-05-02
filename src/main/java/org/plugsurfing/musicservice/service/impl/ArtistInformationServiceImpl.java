package org.plugsurfing.musicservice.service.impl;

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
import org.plugsurfing.musicservice.client.dto.WikipediaInfoDto;
import org.plugsurfing.musicservice.dto.ArtistInformationDto;
import org.plugsurfing.musicservice.service.ArtistInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        final Mono<ArtistMusicBainzInfoDto> artistMBInfo = this.mbClient.getArtistInformationByIdFromMusicBainz(mbId);

        this.mbClient.getArtistInformationByIdFromMusicBainz(mbId) //
                .subscribe(value -> logger.info("Dto->{}", value));

        final Mono<Pair<ArtistMusicBainzInfoDto, String>> mono = artistMBInfo.zipWhen(mbInfo -> {
            final Optional<String> wikidataFromArtistInfo = mbInfo.getRelations().stream()
                    .filter(relation -> relation.getType().equals("wikidata")) //
                    .map(relation -> {
                        final String url = relation.getUrl().getResource();
                        final int lastIndexOf = url.lastIndexOf("/");
                        return url.substring(lastIndexOf + 1);
                    }).findFirst();
            logger.info("WikiData id: {}", wikidataFromArtistInfo.get());
            return this.wikiDataClient.getWikidataForId(wikidataFromArtistInfo.get());
        }, (mbInfo, b) -> new ImmutablePair<ArtistMusicBainzInfoDto, String>(mbInfo, b));

        final Mono<Pair<ArtistMusicBainzInfoDto, WikipediaInfoDto>> wikipediaDataMono = mono.zipWhen(tuple -> {
            final String wikiDataString = tuple.getRight();
            final JSONObject wikiDataJson = new JSONObject(wikiDataString);
            logger.info(this.getWikipediaTitle(wikiDataJson));
            return this.wikipediaClient.getWikipediaInformation(this.getWikipediaTitle(wikiDataJson));
        }, (mbInfo, b) -> new ImmutablePair<ArtistMusicBainzInfoDto, WikipediaInfoDto>(mbInfo.getLeft(), b));

        return wikipediaDataMono.map(tuple -> {
            final WikipediaInfoDto wikipediaData = tuple.getRight();
            final String wikipediaDescription = wikipediaData.getExtract();

            final JSONObject artistInfo = new JSONObject(tuple.getLeft());
            final ArtistInformationDto dto = ArtistInformationDto.builder().mbid(mbId)//
                    .name(artistInfo.getString("name"))//
                    .description(wikipediaDescription)//
                    .country(artistInfo.getString("country"))//
                    .disambiguation(artistInfo.getString("disambiguation"))//
                    .gender(artistInfo.getString("gender")).build();

            return dto;
        });
    }

    private String getWikipediaTitle(final JSONObject wikiDataJson) {

        final String key = wikiDataJson.getJSONObject("entities").keys().next();
        return wikiDataJson.getJSONObject("entities").getJSONObject(key).getJSONObject("sitelinks")
                .getJSONObject("enwiki").getString("title").replace(" ", "_");
    }
}
