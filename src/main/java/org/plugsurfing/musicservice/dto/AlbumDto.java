package org.plugsurfing.musicservice.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class AlbumDto {

    private UUID id;

    private String title;

    private String imageUrl;
}
