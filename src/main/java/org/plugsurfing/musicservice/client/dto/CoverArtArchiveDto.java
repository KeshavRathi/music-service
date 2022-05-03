package org.plugsurfing.musicservice.client.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class CoverArtArchiveDto {

    private UUID id;

    private List<CoverArtImageDto> images;

    @Data
    public static class CoverArtImageDto {

        private Long id;

        private Long edit;

        private String image;
    }
}
