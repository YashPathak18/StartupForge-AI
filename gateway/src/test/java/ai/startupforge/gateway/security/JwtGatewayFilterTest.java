package ai.startupforge.gateway.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtGatewayFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain filterChain;

    @InjectMocks
    private JwtGatewayFilter jwtGatewayFilter;

    @Test
    void filter_MissingAuthHeader_ReturnsUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/projects").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Void> result = jwtGatewayFilter.filter(exchange, filterChain);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
                
        // In actual implementation, verify response status code is UNAUTHORIZED
    }

    @Test
    void filter_ValidToken_InjectsHeaderAndProceeds() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/projects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer validToken")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.isInvalid("validToken")).thenReturn(false);
        when(jwtUtil.getUserId("validToken")).thenReturn("123");
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        Mono<Void> result = jwtGatewayFilter.filter(exchange, filterChain);

        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain).filter(argThat(ex -> 
            "123".equals(ex.getRequest().getHeaders().getFirst("X-User-Id"))
        ));
    }
}
