## 写在前面

​	五一放假期间，粗略的由Spring过度到SpringBoot，上手很舒服。说实话只看了6天的课程，期间与其有关的技术文章看了些许，但是还是感觉很空虚，应该写一个完整的项目去体会一下，所以挑选了一个适合我的项目。于是就有了这个仓库。

​	我是照着B站的课程学的，链接<https://www.bilibili.com/video/BV1y7411y7am>在此。推荐给正在学SpringBoot的小伙伴，一共307集，很感谢这位老师。

# 项目介绍

该项目是采用B2C模式，使用微服务架构，采用前后端分离开发。分为前台网站和后台管理平台。

前台用户系统包括首页数据显示，课程列表和课程详情，课程支付，课程视频播放等；

后台系统包括权限管理、课程管理、课程分类管理等。

简单说就是该有的都有，支付我没写，还有就是我感觉单点登录这块老师讲的不是很完善，生成token存到cookie里面，而且不是严格意义上的跨域，我查询了很多技术博客，越看越意识到真实项目的SSO其实要考虑很多很多问题，而且远远没有这么简单。我借鉴大佬的思路自己重新写了较为完善的SSO。
为完善的SSO。
