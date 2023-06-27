package ru.practicum.shareit.items.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.items.dto.CommentDto;
import ru.practicum.shareit.items.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        String path = "";
        return post(path, userId, itemDto);
    }

    public ResponseEntity<Object> findById(Long itemId, Long userId) {
        String path = "/" + itemId;
        return get(path, userId);
    }

    public ResponseEntity<Object> updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        String path = "/" + itemId;
        return patch(path, ownerId, itemDto);
    }

    public ResponseEntity<Object> getAllUsersItems(Long ownerId, Integer startingEntry, Integer size) {
        String path = "?from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "from", startingEntry,
                "size", size
        );
        return get(path, ownerId, parameters);
    }

    public ResponseEntity<Object> findByNameOrDescription(String text, Integer startingEntry, Integer size) {
        String path = "/search/?text={text}&from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", startingEntry,
                "size", size
        );
        return get(path, null, parameters);
    }

    public ResponseEntity<Object> saveComment(Long bookerId, Long itemId, CommentDto commentDto) {
        String path = "/" + itemId + "/comment";
        return post(path, bookerId, commentDto);
    }
}