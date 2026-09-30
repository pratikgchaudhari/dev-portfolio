package dev.portfolio;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class ReaderIdentity {
    static final String COOKIE = "portfolio_reader";

    public UUID find(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (var cookie : request.getCookies()) {
            if (COOKIE.equals(cookie.getName())) {
                try {
                    var id = UUID.fromString(cookie.getValue());
                    if (id.toString().equalsIgnoreCase(cookie.getValue())) return id;
                } catch (IllegalArgumentException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    public UUID ensure(HttpServletRequest request, HttpServletResponse response) {
        UUID reader = find(request);
        if (reader == null) reader = UUID.randomUUID();
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(COOKIE, reader.toString())
                .httpOnly(true).secure(request.isSecure()).sameSite("Lax").path("/")
                .maxAge(Duration.ofDays(365)).build().toString());
        return reader;
    }
}
