package io.github.ruattd.fc.tweaks;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public class Http {
    static Publisher<Void> route(HttpServerRequest request, HttpServerResponse response) {
        //TODO website
        response.status(HttpResponseStatus.NOT_FOUND);
        return response.send();
    }

    public static String withH5(String htmlText) {
        return "<!DOCTYPE html><html lang=\"zh-CN\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<link href=\"/style.css\" rel=\"stylesheet\">" +
                "</head>" +
                "<body style=\"font-family:sans-serif\">" +
                "<p>" + htmlText + "</p>" +
                "</body>" +
                "</html>";
    }
}
