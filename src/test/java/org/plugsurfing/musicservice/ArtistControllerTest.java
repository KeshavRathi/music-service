package org.plugsurfing.musicservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.plugsurfing.musicservice.dto.ArtistInformationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
public class ArtistControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testValidInput() {
        this.webTestClient.get().uri("/music-artist/details/f27ec8db-af05-4f36-916e-3d57f91ecf5e")
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
                .expectBody(ArtistInformationDto.class);
        ;
    }

    @Test
    public void testInValidInput() {
        this.webTestClient.get().uri("/music-artist/details/f27ec8db-af05-4f36-916e-3d57f91ecf6e")
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().is5xxServerError()
                .expectBody(String.class)
                .value(error -> assertThat(error.equals("Artist not found, Please share a valid ID")));
    }

}