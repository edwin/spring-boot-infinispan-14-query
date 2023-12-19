package com.edw.helper;

import com.edw.bean.User;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <pre>
 *     com.edw.helper.GenerateCacheHelper
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 19 Des 2023 13:54
 */
@Service
public class GenerateCacheHelper {

    @Autowired
    private RemoteCacheManager cacheManager;

    public void sendToCache() {
        final RemoteCache cache = cacheManager.getCache("user-cache");

        Map<String, User> hashMap = new HashMap<>();
        for (int j = 0; j < 1000; j++) {
            hashMap.put(UUID.randomUUID().toString(), new User(UUID.randomUUID().toString(), 17, "Jakarta"));
        }
        cache.putAll(hashMap);
    }

    public List<User> getUsersFromCity(String address) {
        RemoteCache remoteCache = cacheManager.getCache("user-cache");
        QueryFactory queryFactory = Search.getQueryFactory(remoteCache);

        Query<User> query = queryFactory.create("FROM user.User WHERE address like :address ORDER BY name ASC, age DESC");
        query.setParameter("address", address);

        return query.execute().list();
    }
}
