package ru.practicum.shareit.bookings.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.BaseClient;
import ru.practicum.shareit.bookings.dto.BookingDto;
import ru.practicum.shareit.bookings.dto.State;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingDto bookingDto) {
        String path = "";
        return post(path, userId, bookingDto);
    }

    public ResponseEntity<Object> confirmBooking(Long ownerId, Long bookingId, boolean isApproved) {
        String path = "/" + bookingId + "?approved={approved}";
        Map<String, Object> parameters = Map.of(
                "approved", isApproved
        );
        return patch(path, ownerId, parameters, null);
    }

    public ResponseEntity<Object> findById(Long userId, Long bookingId) {
        String path = "/" + bookingId;
        return get(path, userId);
    }

    public ResponseEntity<Object> findAllUsersBookingsByState(Long bookerId, State state,
                                                              Integer startingEntry, Integer size) {
        String path = "?state={state}&from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", startingEntry,
                "size", size
        );
        return get(path, bookerId, parameters);
    }

    public ResponseEntity<Object> findAllOwnersBookingsByState(Long ownerId, State state,
                                                               Integer startingEntry, Integer size) {
        String path = "/owner?state={state}&from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", startingEntry,
                "size", size
        );
        return get(path, ownerId, parameters);
    }
}