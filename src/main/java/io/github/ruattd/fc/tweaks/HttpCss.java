package io.github.ruattd.fc.tweaks;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpCss {
    private static String styleCss = null;

    static {
        try {
            var url = HttpCss.class.getResource("/assets/fctweaks/style.css");
            if (url != null) {
                var stream = url.openStream();
                styleCss = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {}
    }

    static Publisher<Void> route(HttpServerRequest ignored, HttpServerResponse response) {
        if (styleCss == null) {
            response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            return response.send();
        } else {
            response.status(HttpResponseStatus.OK);
            response.addHeader("Content-Type", "text/css");
            return response.sendString(Mono.just(styleCss));
        }
    }
}
