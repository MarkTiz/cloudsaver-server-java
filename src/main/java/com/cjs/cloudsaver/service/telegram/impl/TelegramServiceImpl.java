package com.cjs.cloudsaver.service.telegram.impl;

import com.cjs.cloudsaver.service.telegram.TelegramService;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class TelegramServiceImpl implements TelegramService {

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void getImages( String url, HttpServletResponse response) throws IOException {
        if (url == null || url.trim().isEmpty()||"undefined".equals(url)) {
            return;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response okHttpResponse = client.newCall(request).execute()) {
            if (!okHttpResponse.isSuccessful()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Image fetch error");
                return;
            }

            String contentType = okHttpResponse.header("Content-Type");
            if (contentType != null) {
                response.setContentType(contentType);
            }

            assert okHttpResponse.body() != null;
            try (InputStream inputStream = okHttpResponse.body().byteStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, bytesRead);
                }
                response.getOutputStream().flush();
            }
        }
    }
}
