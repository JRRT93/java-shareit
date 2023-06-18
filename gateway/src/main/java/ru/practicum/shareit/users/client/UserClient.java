package ru.practicum.shareit.users.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.users.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDto requestDto) {
        String path = "";
        return post(path, requestDto);
    }

    public ResponseEntity<Object> findById(Long id) {
        String path = "/" + id;
        return get(path, id);
    }

    public ResponseEntity<Object> update(UserDto requestDto, Long id) {
        String path = "/" + id;
        return patch(path, id, requestDto);
    }

    public ResponseEntity<Object> deleteById(Long id) {
        String path = "/" + id;
        return delete(path);
    }

    public ResponseEntity<Object> findAll() {
        String path = "";
        return get(path);
    }
}