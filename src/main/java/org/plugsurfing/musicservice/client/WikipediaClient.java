package org.plugsurfing.musicservice.client;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class WikipediaClient {

    private WebClient client;

    @PostConstruct
    public void init() {
        this.client = WebClient.builder().baseUrl("https://en.wikipedia.org/api").build();
    }

    public Mono<String> getWikipediaInformation(final String title) {

        final String uri = "/rest_v1/page/summary/" + title; // URI Builder can also be
                                                             // used
//        final ClientResponse block = this.client.get().uri(uri).exchange().block();
        return this.client.get().uri(uri)//
//                .accept(MediaType.APPLICATION_JSON)//
                .retrieve()//
                .bodyToMono(String.class);
    }
}
