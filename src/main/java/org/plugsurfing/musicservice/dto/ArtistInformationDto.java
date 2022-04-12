package org.plugsurfing.musicservice.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistInformationDto {

    private UUID mbid;

    private String name;

    private String gender;

    private String country;

    private String disambiguation;

    private String description;

    @Builder.Default
    private List<AlbumDto> albums = new ArrayList<>();

}
