package com.cjs.cloudsaver.controller.douban;

import com.cjs.cloudsaver.model.douban.DoubanSubject;
import com.cjs.cloudsaver.service.douban.DoubanService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DoubanController {

    @Resource
    private DoubanService doubanService;

    @GetMapping("/douban/hot")
    public List<DoubanSubject> searchHot(
            @RequestParam(required = false) String type,
            @RequestParam String tag,
            @RequestParam(name = "page_limit", defaultValue = "50") int pageLimit,
            @RequestParam(name = "page_start", defaultValue = "0") int pageStart) {
        return doubanService.getHotList(type, tag, pageLimit, pageStart);
    }
}
