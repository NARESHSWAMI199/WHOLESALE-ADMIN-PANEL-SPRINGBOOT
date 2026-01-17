package com.sales.jwtUtils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenTest {

    @Test
    void generateAndParseToken() throws Exception {
        JwtToken jwt = new JwtToken();
        // set secret via reflection
        Field secret = JwtToken.class.getDeclaredField("secret");
        secret.setAccessible(true);
        secret.set(jwt, "test-secret-abc");

        String token = jwt.generateToken("myslug");
        assertNotNull(token);

        String slug = jwt.getSlugFromToken(token);
        assertEquals("myslug", slug);

        Date exp = jwt.getExpirationDateFromToken(token);
        assertTrue(exp.after(new Date()));
        assertFalse(jwt.isTokenExpired(token));
    }
}
