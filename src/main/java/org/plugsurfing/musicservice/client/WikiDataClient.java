package org.plugsurfing.musicservice.client;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class WikiDataClient {

    private WebClient client;

    @PostConstruct
    public void init() {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size)).build();
        this.client = WebClient.builder().baseUrl("https://www.wikidata.org/w/api.php").exchangeStrategies(strategies)
                .build();
    }

    // https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&ids=Q42&sites=&props=sitelinks&sitefilter=enwiki
    public Mono<String> getWikidataForId(final String id) {

        final String uri = "?action=wbgetentities&format=json&ids=" + id + "&props=sitelinks&sitefilter=enwiki"; // URI
                                                                                                                 // Builder
                                                                                                                 // can
                                                                                                                 // also
                                                                                                                 // be
                                                                                                                 // used
        return this.client.get().uri(uri)//
//                .accept(MediaType.APPLICATION_JSON)//
                .retrieve()//
                .bodyToMono(String.class);
    }
}
