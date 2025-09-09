package com.cjs.cloudsaver.service.search;

import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.model.search.ChannelResult;

import java.util.List;

public interface SearchService {
    List<ChannelResult> searchAll(String keyword, String channelId, String lastMessageId)throws BizException;

    Object get115LinkByTmdbId(String tmdbId, String type);
}
