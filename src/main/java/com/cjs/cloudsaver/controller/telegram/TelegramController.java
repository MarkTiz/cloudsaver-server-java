package com.cjs.cloudsaver.controller.telegram;

import com.cjs.cloudsaver.service.telegram.TelegramService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TelegramController {
    @Resource
    private TelegramService telegramService;

    /**
     * 获取图片
     */
    @GetMapping("/tele-images/")
    public void getImages(@RequestParam("url") String url, HttpServletResponse response) throws IOException {
        telegramService.getImages(url, response);
    }
}
