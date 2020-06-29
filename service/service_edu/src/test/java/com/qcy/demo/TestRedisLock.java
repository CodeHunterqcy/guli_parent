package com.qcy.demo;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author CodeHunter_qcy
 * @date 2020/6/29 - 17:13
 */
public class TestRedisLock {
    private static CountDownLatch countDownLatch = new CountDownLatch(99);

    public void test() throws InterruptedException {
    TicketsRunBle ticketsRunBle = new TicketsRunBle();
        for (int i = 0; i < 99; i++) {

            Thread thread = new Thread(ticketsRunBle,"窗口"+i);
            thread.start();
            countDownLatch.countDown();
        }
        Thread.currentThread().join();
    }

    public class TicketsRunBle implements Runnable {
        @Override
        public void run() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

/*
            RedisTemplate redisTemplate  =new RedisTemplate();
            List<HttpMessageConverter<?>> fastJsonHttpMessageConverters = new ArrayList<>();
            fastJsonHttpMessageConverters.add(new FastJsonHttpMessageConverter());
            redisTemplate.setMessageConverters(fastJsonHttpMessageConverters);
            R forObject  = redisTemplate.getForObject();
            System.out.println(forObject);*/
        }
    }

}
