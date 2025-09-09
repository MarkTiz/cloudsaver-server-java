package com.cjs.cloudsaver.service.douban;

import com.cjs.cloudsaver.model.douban.DoubanSubject;

import java.util.List;

public interface DoubanService {

    List<DoubanSubject> getHotList(String type, String tag, int pageLimit, int pageStart);
}
