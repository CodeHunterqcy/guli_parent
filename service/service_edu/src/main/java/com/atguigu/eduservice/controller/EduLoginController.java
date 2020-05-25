package com.atguigu.eduservice.controller;

import com.atguigu.commonutils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
@Api(tags = "登录接口")
//登录接口
@CrossOrigin//解决跨域
@RestController
@RequestMapping("eduservice/user")
public class EduLoginController {
    //login
    @ApiOperation(value = "登录")
    @PostMapping("login")
    public R login(){
        return R.ok().data("token","admin");
    }
    //info
    @GetMapping("info")
    public R info(){
        //前端要roles,name，avatar
        return R.ok().data("roles","[admin]").data("name","admin").data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
    }

}
