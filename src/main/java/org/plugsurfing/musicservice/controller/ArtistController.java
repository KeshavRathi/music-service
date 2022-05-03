package org.plugsurfing.musicservice.controller;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.plugsurfing.musicservice.dto.ArtistInformationDto;
import org.plugsurfing.musicservice.service.ArtistInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/music-artist")
public class ArtistController {

    private static final Logger logger = LogManager.getLogger(ArtistController.class);

    @Autowired
    private ArtistInformationService artistInformationService;

    @GetMapping("/details/{mbid}")
    @Cacheable("artist-information")
    public Mono<ArtistInformationDto> getArtistDetails(@PathVariable final UUID mbid) {
        logger.info("Received mbid : {}", mbid);
        final ArtistInformationDto artistInformationDto = new ArtistInformationDto();
        artistInformationDto.setMbid(mbid);
        return this.artistInformationService.getArtistInformation(mbid);
    }
}
