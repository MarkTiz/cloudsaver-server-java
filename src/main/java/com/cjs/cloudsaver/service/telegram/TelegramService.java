package com.cjs.cloudsaver.service.telegram;

import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;

public interface TelegramService {

    void getImages(String url, HttpServletResponse response) throws IOException;
}
