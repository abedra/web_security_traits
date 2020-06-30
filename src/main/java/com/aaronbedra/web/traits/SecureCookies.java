package com.aaronbedra.web.traits;

import com.aaronbedra.web.WebRequester;
import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.traitor.traits.Trait;
import okhttp3.Cookie;
import okhttp3.Response;

import java.util.List;

import static com.jnape.palatable.lambda.io.IO.io;
import static org.junit.Assert.assertTrue;

public class SecureCookies implements Trait<WebRequester> {
    @Override
    public void test(WebRequester requester) {
        requester.request().<IO<Response>>runReaderT(requester.getHttpsUrl())
                .flatMap(response -> requester.getCookieJar()
                        .flatMap(cookieJar -> assertSecure(cookieJar.loadForRequest(response.request().url()))))
                .unsafePerformIO();
    }

    private IO<Unit> assertSecure(List<Cookie> cookies) {
        return io(() -> cookies.forEach(cookie -> {
            assertTrue(cookie.name() + " not marked secure", cookie.secure());
            assertTrue(cookie.name() + "not marked HttpOnly", cookie.httpOnly());
        }));
    }
}
