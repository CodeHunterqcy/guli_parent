package com.atguigu.eduservice.filter;

import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.mapper.EduCourseMapper;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author CodeHunter_qcy
 * @date 2020/6/29 - 0:45
 */
@Component
public class RedisBloomFilter {
    //预计插入量
    private long size = 1000;
    //误判率
    private double fpp = 0.001F;
    //位数组长度
    private long numBits;
    //hash数量
    private int numHashFunctions;
    //redis中的key
    private final String key = "course_filter";
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private EduCourseService courseService;


    @PostConstruct
    private void init() {
        this.numBits = optimalNumOfBits(size, fpp);
        this.numHashFunctions = optimalNumOfHashFunctions(size, numBits);
        List<EduCourse> list = courseService.list(null);
        for (EduCourse course:list){
            this.put(course.getId());
        }
    }

    //计算hash函数的个数
    private int optimalNumOfHashFunctions(long n, long m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    //计算bit数组长度
    private long optimalNumOfBits(long n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (long) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    //向布隆过滤器中put
    public void put(String id) {
        long[] indexs = getIndexs(id);
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                //打开管道，提高效率
                redisConnection.openPipeline();
                for (long index:indexs){
                    redisConnection.setBit(key.getBytes(),index,true);
                }
                redisConnection.close();
                return null;
            }
        });
    }

    //判断id是否可能存在
    public boolean isExist(String id) {
        long[] indexs = getIndexs(id);
       List list =redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                //打开管道，提高效率
                redisConnection.openPipeline();
                for (long index:indexs){
                  redisConnection.getBit(key.getBytes(),index);
                }
                redisConnection.close();
                return null;
            }
        });
       return !list.contains(false);
    }

    //根据key获取bitmap下标(算法借鉴)
    private long[] getIndexs(String key) {
        long hash1 = hash(key);
        long hash2 = hash1 >>> 16;
        long[] result = new long[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            long combinedHash = hash1 + i * hash2;
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            result[i] = combinedHash % numBits;
        }
        return result;
    }

    //计算哈希值(算法借鉴)
    private long hash(String key) {
        Charset charset = Charset.defaultCharset();
        return Hashing.murmur3_128().hashObject(key, Funnels.stringFunnel(charset)).asLong();
    }

    //计算二进制向量大小(算法借鉴)
    private long optimalNumOfBits() {
        return (long) ((double) (-size) * Math.log(fpp) / (Math.log(2.0D) * Math.log(2.0D)));
    }

    //计算哈希算法数量(算法借鉴)
    private int optimalNumOfHashFunctions() {
        return Math.max(1, (int) Math.round((double) numBits / (double) size * Math.log(2.0D)));
    }

    public static void main(String[] args) {

    }
}
