package org.plugsurfing.musicservice.service;

import java.util.UUID;

import org.plugsurfing.musicservice.dto.ArtistInformationDto;

import reactor.core.publisher.Mono;

public interface ArtistInformationService {

    Mono<ArtistInformationDto> getArtistInformation(UUID mbId);
}
