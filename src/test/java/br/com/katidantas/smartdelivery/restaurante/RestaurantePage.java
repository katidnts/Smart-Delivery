package br.com.katidantas.smartdelivery.restaurante;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantePage extends PageImpl<DadosListaRestauranteDTO> {

    @JsonCreator
    public RestaurantePage(
            @JsonProperty("content") List<DadosListaRestauranteDTO> content,
            @JsonProperty("page") Map<String, Object> page) {
        super(content,
                PageRequest.of(
                        (int) page.get("number"),
                        (int) page.get("size")
                ),
                ((Number) page.get("totalElements")).longValue()
        );

    }
}
