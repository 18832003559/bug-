package com.example.concurrent;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author houyangfan
 * @version 1.0
 * @date 2022/6/29 16:33
 *
 *
 */
@RestController
@RequestMapping("concurrentHashMap")
public class ConcurrentHashMapBug {

    // 总元素数量
    private static int ITEM_AMOUNT = 1000;
    // 线程个数
    private static int THREAD_AMOUNT = 10;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor (15, 100, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1000));

    public ConcurrentHashMap<String, Long> getData(int count){
        return LongStream.rangeClosed(1, count)
                .boxed()
                .collect(Collectors.toConcurrentMap(i -> UUID.randomUUID().toString(), Function.identity(),
                        (o1, o2) -> o1, ConcurrentHashMap::new));
    }

    @GetMapping("bugTest")
    public Integer testBug(){

        // 初始化map容器中的数据
        ConcurrentHashMap<String, Long> mapData = getData(ITEM_AMOUNT - 100);
        System.out.println("初始化数量："+ mapData.size());
        for (int i = 0; i < 10; i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                  //查询还需要补充多少元素
                    int gap = ITEM_AMOUNT - mapData.size();
                    System.out.println("gap size:{}"+ gap);
                    //补充元素
                    mapData.putAll(getData(gap));
                }
            });
        }
    }

}
