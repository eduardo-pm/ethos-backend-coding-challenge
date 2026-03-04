package com.ethos.backoffice.infrastructure.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateLimiterFilterTest {

    private RateLimiterFilter rateLimiterFilter;

    @BeforeEach
    void setUp() {
        rateLimiterFilter = new RateLimiterFilter(3, 60);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowRequests_WhenUnderLimit() throws Exception {
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest request = buildRequest("192.168.1.1");
            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);

            rateLimiterFilter.doFilter(request, response, filterChain);

            assertThat(response.getStatus()).isNotEqualTo(429);
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void shouldBlockRequest_WhenOverLimit() throws Exception {
        for (int i = 0; i < 3; i++) {
            rateLimiterFilter.doFilter(buildRequest("10.0.0.1"), new MockHttpServletResponse(), mock(FilterChain.class));
        }

        MockHttpServletRequest blockedRequest = buildRequest("10.0.0.1");
        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        FilterChain blockedChain = mock(FilterChain.class);

        rateLimiterFilter.doFilter(blockedRequest, blockedResponse, blockedChain);

        assertThat(blockedResponse.getStatus()).isEqualTo(429);
        verify(blockedChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldTrackDifferentIpsSeparately() throws Exception {
        for (int i = 0; i < 3; i++) {
            rateLimiterFilter.doFilter(buildRequest("1.1.1.1"), new MockHttpServletResponse(), mock(FilterChain.class));
        }

        MockHttpServletRequest differentIp = buildRequest("2.2.2.2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        rateLimiterFilter.doFilter(differentIp, response, filterChain);

        assertThat(response.getStatus()).isNotEqualTo(429);
        verify(filterChain).doFilter(differentIp, response);
    }

    @Test
    void shouldTrackByUsername_WhenAuthenticated() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user@test.com", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest request = buildRequest("192.168.1.1");
            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);

            rateLimiterFilter.doFilter(request, response, filterChain);

            assertThat(response.getStatus()).isNotEqualTo(429);
        }

        MockHttpServletRequest request = buildRequest("192.168.1.1");
        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        FilterChain blockedChain = mock(FilterChain.class);

        rateLimiterFilter.doFilter(request, blockedResponse, blockedChain);

        assertThat(blockedResponse.getStatus()).isEqualTo(429);
    }

    @Test
    void shouldUseXForwardedForHeader_WhenPresent() throws Exception {
        MockHttpServletRequest request = buildRequest("10.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.5, 10.0.0.1");

        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest req = buildRequest("10.0.0.1");
            req.addHeader("X-Forwarded-For", "203.0.113.5, 10.0.0.1");
            rateLimiterFilter.doFilter(req, new MockHttpServletResponse(), mock(FilterChain.class));
        }

        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        rateLimiterFilter.doFilter(request, blockedResponse, mock(FilterChain.class));

        assertThat(blockedResponse.getStatus()).isEqualTo(429);
    }

    private MockHttpServletRequest buildRequest(String remoteAddr) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddr);
        return request;
    }
}
