## 写在前面

​	五一放假期间，粗略的由Spring过度到SpringBoot，上手很舒服。说实话只看了6天的课程，期间与其有关的技术文章看了些许，但是还是感觉很空虚，应该写一个完整的项目去体会一下，所以挑选了一个适合我的项目。于是就有了这个仓库。

​	我是照着B站的课程学的，[链接在此](https://www.bilibili.com/video/BV1y7411y7am)。推荐给正在学SpringBoot的小伙伴，一共307集，很感谢这位老师。

# 项目介绍

该项目是采用B2C模式，使用微服务架构，采用前后端分离开发。分为前台网站和后台管理平台。

前台用户系统包括首页数据显示，课程列表和课程详情，课程支付，课程视频播放等；

后台系统包括权限管理、课程管理、课程分类管理等。

涉及到的技术有Spring Boot+Spring Cloud+MyBatis-Plus+MySQL+nginx+EasyExcel+redis

前端用的是vue框架，下载好VSCode和node.js
后端端采用微服务架构，持久层使用的是MyBatis-Plus，采用更便捷的Nacos作为注册中心和配置中心，用Feign实现微服务远程调用，Swagger生成接口文档方便测试和前台调用，使用EasyExcel对课程分类进行批量添加，用阿里云OSS存储个人头像，接入阿里云视频点播实现的课程播放，使用JWT实现分布式站点的单点登录，用redis缓存首页数据，使用Nginx做请求转发，用Spring Security实现权限管理。

课程支付我没写，还有就是我感觉单点登录这块老师讲的不是很完善，生成token存到cookie里面，而且不是严格意义上的跨域，我查询了很多技术博客，越看越意识到真实项目的SSO其实要考虑很多很多问题，而且远远没有这么简单。我借鉴大佬的思路自己重新写了较为完善的SSO。
代码生成器太爽了，MP用的也很爽，各种整合，总体来说Spring Boot上手很舒服，但是要在学完Spring和SpringMVC的前提下去过度。

redis这块，没有做缓存一致性，我自己设置了几个场景来学习缓存击穿、缓存穿透、缓存雪崩，当然只是玩具例子，比如缓存击穿，一般的项目根本没有能达到击穿要求的热点数据。还搞了一个布隆过滤器，然后了解了一下redis集群的hash一致性算法，第一回见到hash环这种想法。

然后就是SpringBoot本体了，前后端分离开发，RESTful规范接口，swagger2太好用了。

# 项目

## 前台页面
### 登录注册
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200627014550337.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200627014639207.png)
## 首页
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200627015336272.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200627015412163.png)
## 课程页面
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200627015534475.png)
## 课程详情
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200627015650543.png)
## 后台前端页面
登录注册界面略过
## 讲师管理

#### 讲师列表
![讲师列表](https://img-blog.csdnimg.cn/20200627011850158.png)
#### 添加讲师
![添加讲师](https://img-blog.csdnimg.cn/20200627012649277.png)

## 课程分类管理

### 上传excel添加课程分类
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200627012801638.png)

## 课程管理

### 添加课程
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200627013013542.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200627013108382.png)
### 发布课程
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200627013220613.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200627013606334.png)

## 实现常用课程LRU类
	
	
	
	/**
	 * @author CodeHunter_qcy
	 * @date 2020/6/18 - 23:03
	 */
	
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	
	public class LRU<K, V> {
	    private int currentSize;
	    private int capcity;
	    private HashMap<K, Node> caches;
	    private Node first;
	    private Node last;
	
	    public LRU(int size) {
	        this.currentSize = 0;
	        this.capcity = size;
	        caches = new HashMap<K, Node>(size);
	    }
	
	    public void put(K key, V value) {
	        Node node = caches.get(key);
	
	        if (null == node) {
	            if (caches.size() >= capcity) {
	                caches.remove(last.key);
	                removeLast();
	            }
	            node = new Node(key, value);
	            caches.put(key, node);
	            currentSize++;
	        } else {
	            node.value = value;
	        }
	        moveToHead(node);
	    }
	
	    public Object get(K key) {
	        Node node = caches.get(key);
	        if (node == null) {
	            return null;
	        }
	        moveToHead(node);
	        return node.value;
	    }
	
	    public Object remove(K key) {
	        Node node = caches.get(key);
	        if (node != null) {
	            if (node.pre != null) {
	                node.pre.next = node.next;
	            }
	            if (node.next != null) {
	                node.next.pre = node.pre;
	            }
	            if (node == first) {
	                first = node.next;
	            }
	            if (node == last) {
	                last = node.pre;
	            }
	        }
	        return caches.remove(key);
	    }
	
	    private void moveToHead(Node node) {
	        if (first == node) {
	            return;
	        }
	        if (node.pre != null) {
	            node.pre.next = node.next;
	        }
	        if (node.next != null) {
	            node.next.pre = node.pre;
	        }
	
	        if (node == last) {
	            last = node.pre;
	        }
	        if (first == null || last == null) {
	            first = last = node;
	            return;
	        }
	        node.next = first;
	        first.pre = node;
	        first = node;
	        first.pre = null;
	
	    }
	
	    private void removeLast() {
	        if (last != null) {
	            last = last.pre;
	            if (last == null) {
	                first = null;
	            } else {
	                last.next = null;
	            }
	        }
	    }
	
	    public String toString() {
	        StringBuilder sb = new StringBuilder();
	        Node node = first;
	        while (node != null) {
	            sb.append(String.format("%s:  ", node.key));
	            node = node.next;
	        }
	        return sb.toString();
	    }
	
	    public void clear() {
	        first = null;
	        last = null;
	        caches.clear();
	    }
	    public List getList(){
	        ArrayList list = new ArrayList();
	        Node node = first;
	        while (node!=null){
	            list.add(node.value);
	            node = node.next;
	        }
	        return list;
	    }
	
	}
	
	class Node {
	    Object key;
	    Object value;
	    Node pre;
	    Node next;
	
	    public Node(Object key, Object value) {
	        super();
	        this.key = key;
	        this.value = value;
	    }
	
	}
	
	
	

## 方案一解决缓存穿透
```

   public CourseWebVo getBaseCourseInfo(String courseId) {
        //redis拿
        Object redisObj = valueOperations.get(courseId);
        //命中缓存
        if (null!=redisObj){
            if (redisObj instanceof NullValueResult){
                System.out.println("是空对象");
                return null;
            }
            System.out.println("从缓存拿到的");
            return (CourseWebVo) redisObj;
        }
        try{
            CourseWebVo courseWebVo =  baseMapper.getBaseCourseInfo(courseId);

            if (courseWebVo!=null){
                System.out.println("数据库查到，写入缓存中");
                valueOperations.set(courseId,courseWebVo,10,TimeUnit.MINUTES);
                return courseWebVo;
            }else{
                System.out.println("数据库没有，缓存空对象，解决缓存穿透");
                valueOperations.set(courseId,new NullValueResult(),10,TimeUnit.MINUTES);
            }
        }finally {

        }
        return null;
    }
```
