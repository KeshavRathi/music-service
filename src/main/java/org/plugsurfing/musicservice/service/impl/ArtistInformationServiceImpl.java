package org.plugsurfing.musicservice.service.impl;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.plugsurfing.musicservice.client.CoverArtArchiveClient;
import org.plugsurfing.musicservice.client.MusicBainzClient;
import org.plugsurfing.musicservice.client.WikiDataClient;
import org.plugsurfing.musicservice.client.WikipediaClient;
import org.plugsurfing.musicservice.dto.ArtistInformationDto;
import org.plugsurfing.musicservice.service.ArtistInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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

        final Mono<String> artistMBInfo = this.mbClient.getArtistInformationByIdFromMusicBainz(mbId);
        artistMBInfo.subscribe(value -> {
            final JSONObject artistInfo = new JSONObject(value);
            final String name = artistInfo.getString("name");
            logger.info("Name: {}", name);
        });

        final Mono<Tuple2<String, String>> mono = artistMBInfo.zipWhen(mbInfo -> {
            final JSONObject artistInfo = new JSONObject(mbInfo);
            final JSONArray artistRelations = artistInfo.getJSONArray("relations");
            final String wikidataFromArtistInfo = this.getWikidataFromArtistInfo(artistRelations);
            logger.info("WikiData: {}", wikidataFromArtistInfo);
            return this.wikiDataClient.getWikidataForId(wikidataFromArtistInfo);
        });

        final Mono<Tuple2<Tuple2<String, String>, String>> wikipediaDataMono = mono.zipWhen(tuple -> {
            final String wikiDataString = tuple.getT2();
            final JSONObject wikiDataJson = new JSONObject(wikiDataString);
            logger.info(this.getWikipediaTitle(wikiDataJson));
            return this.wikipediaClient.getWikipediaInformation(this.getWikipediaTitle(wikiDataJson));
        });

        return wikipediaDataMono.map(tuple -> {
            final String wikipediaData = tuple.getT2();
            final JSONObject wikipediaJson = new JSONObject(wikipediaData);
            final String wikipediaDescription = this.getWikipediaDescription(wikipediaJson);

            final JSONObject artistInfo = new JSONObject(tuple.getT1().getT1());
            final ArtistInformationDto dto = ArtistInformationDto.builder().mbid(mbId)//
                    .name(artistInfo.getString("name"))//
                    .description(wikipediaDescription)//
                    .country(artistInfo.getString("country"))//
                    .disambiguation(artistInfo.getString("disambiguation"))//
                    .gender(artistInfo.getString("gender")).build();

            return dto;
        });
    }

    private String getWikidataFromArtistInfo(final JSONArray jsonArray) {

        for (int i = 0; i < jsonArray.length(); i++) {
            final JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("type").equals("wikidata")) {
                final String url = jsonObject.getJSONObject("url").getString("resource");
                final int lastIndexOf = url.lastIndexOf("/");
                return url.substring(lastIndexOf + 1);
            }
        }
        throw new RuntimeException("Data Not found");
    }

    private String getWikipediaTitle(final JSONObject wikiDataJson) {

        final String key = wikiDataJson.getJSONObject("entities").keys().next();
        return wikiDataJson.getJSONObject("entities").getJSONObject(key).getJSONObject("sitelinks")
                .getJSONObject("enwiki").getString("title").replace(" ", "_");
    }

    private String getWikipediaDescription(final JSONObject wikiDataJson) {

        return wikiDataJson.getString("extract");
    }

}