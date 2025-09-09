package com.cjs.cloudsaver.controller.search;

import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.model.search.ChannelResult;
import com.cjs.cloudsaver.service.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {


    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public List<ChannelResult> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "") String channelId,
            @RequestParam(defaultValue = "") String lastMessageId) throws BizException {
            return searchService.searchAll(keyword, channelId, lastMessageId);
    }

    @GetMapping("/{type}/{tmdbId}/115")
    public Object search(
            @PathVariable String tmdbId,
            @PathVariable String type) {
        return searchService.get115LinkByTmdbId(tmdbId, type);
    }


}
