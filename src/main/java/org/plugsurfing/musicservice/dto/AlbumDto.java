package org.plugsurfing.musicservice.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumDto {

    private UUID id;

    private String title;

    private String imageUrl;
}
