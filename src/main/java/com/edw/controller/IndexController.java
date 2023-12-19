package com.edw.controller;

import com.edw.bean.User;
import com.edw.helper.GenerateCacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * <pre>
 *     com.edw.controller.IndexController
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 19 Des 2023 13:47
 */
@RestController
public class IndexController {

    @Autowired
    private GenerateCacheHelper generateCacheHelper;

    @GetMapping(path = "/")
    public HashMap index() {
        return new HashMap(){{
            put("hello", "world");
        }};
    }

    @GetMapping(path = "/get-users-from-city")
    public List<User> getUsersFromCity(@RequestParam String address) {
        return generateCacheHelper.getUsersFromCity(address);
    }


    @GetMapping(path = "/generate")
    public String generateCacheToRHDG() {
        generateCacheHelper.sendToCache();
        return "good";
    }
}
