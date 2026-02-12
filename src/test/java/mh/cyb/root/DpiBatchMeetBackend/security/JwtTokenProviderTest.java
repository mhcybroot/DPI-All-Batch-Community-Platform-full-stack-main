package mh.cyb.root.DpiBatchMeetBackend.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JwtTokenProviderTest {

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

    @Test
    public void testGenerateAndValidateToken() {
        String username = "testuser";
        String token = jwtTokenProvider.generateToken(username);

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
        Assertions.assertTrue(jwtTokenProvider.validateToken(token));
        Assertions.assertEquals(username, jwtTokenProvider.getUsername(token));
    }
}
