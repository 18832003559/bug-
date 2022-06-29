package com.example.concurrent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author houyangfan
 * @version 1.0
 * @date 2022/6/29 11:05
 *  ThreadLocal 适用于变量在线程间隔离，而在方法或类间共享的场景,比如在业务中获取用户信息比较昂贵的情况下可以将用户信息放入threadLocal
 *  本示例是记录线程重用导致获取用户信息错乱的情况，可以通过 设置 server.tomcat.threads.max =1 来设置tomcat线程数量
 *  该例是将一个Integer值放入来代表存入threadlocal中的共享信息，初始值为null
 *  解决：finally 中在用完ThreadLocal后，清空当前线程的信息
 */
@RestController
@RequestMapping("/threadLocal")
public class ThreadLocalBug {

    public static final ThreadLocal<Integer> currentInfo = ThreadLocal.withInitial(()->null);

    @GetMapping("/bugTest")
    public Map<String, String> testBug(Integer userId){
        Map<String, String> res;
            //设置用户信息之前先查一次
            String beforeInfo = Thread.currentThread().getName() +":"+ currentInfo.get();
            currentInfo.set(userId);
            // 设置之后再查
            String afterInfo = Thread.currentThread().getName() + ":" + currentInfo.get();

            res = new HashMap<>();
            res.put("before", beforeInfo);
            res.put("after", afterInfo);
        return res;

    }

    @GetMapping("/sucTest")
    public Map<String, String> testSuc(Integer userId){
        Map<String, String> res;
        try {
            //设置用户信息之前先查一次
            String beforeInfo = Thread.currentThread().getName() +":"+ currentInfo.get();
            currentInfo.set(userId);
            // 设置之后再查
            String afterInfo = Thread.currentThread().getName() + ":" + currentInfo.get();

            res = new HashMap<>();
            res.put("before", beforeInfo);
            res.put("after", afterInfo);
        } finally {
            // 在用完ThreadLocal后，清空当前线程的信息
            currentInfo.remove();
        }
        return res;

    }
}