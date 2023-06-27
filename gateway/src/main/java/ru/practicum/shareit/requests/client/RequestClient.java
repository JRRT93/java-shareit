package ru.practicum.shareit.requests.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItemRequest(Long authorId, ItemRequestDto itemRequestDto) {
        String path = "";
        return post(path, authorId, itemRequestDto);
    }

    public ResponseEntity<Object> findById(Long requestId, Long askerId) {
        String path = "/" + requestId;
        return get(path, askerId);
    }

    public ResponseEntity<Object> findMyItemRequests(Long authorId) {
        String path = "";
        return get(path, authorId);
    }

    public ResponseEntity<Object> findTheirItemRequest(Long authorId, Integer startingEntry, Integer size) {
        String path = "/all?from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "from", startingEntry,
                "size", size
        );
        return get(path, authorId, parameters);
    }
}