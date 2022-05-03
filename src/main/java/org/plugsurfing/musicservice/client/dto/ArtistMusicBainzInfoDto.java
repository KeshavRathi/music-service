package org.plugsurfing.musicservice.client.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class ArtistMusicBainzInfoDto {

    @JsonAlias("id")
    private UUID id;

    @JsonAlias("name")
    private String name;

    @JsonAlias("gender")
    private String gender;

    @JsonAlias("country")
    private String country;

    @JsonAlias("disambiguation")
    private String disambiguation;

    @JsonAlias("release-groups")
    private List<ReleaseGroupsDto> releaseGroups;

    @JsonAlias("relations")
    private List<RelationsDto> relations;

    @Data
    public static class ReleaseGroupsDto {

        @JsonAlias("id")
        private UUID id;

        @JsonAlias("title")
        private String title;

    }

    @Data
    public static class RelationsDto {

        @JsonAlias("id")
        private UUID id;

        @JsonAlias("type")
        private String type;

        @JsonAlias("url")
        private RelationsUrlDto url;

    }

    @Data
    public static class RelationsUrlDto {

        @JsonAlias("resource")
        private String resource;
    }

}
