package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.subject.OneSubject;
import com.atguigu.eduservice.service.EduSubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author CodeHunter_qcy
 * @since 2020-05-19
 */
@Api(tags = "课程分类接口")
@RestController
@RequestMapping("/eduservice/subject")
@CrossOrigin
public class EduSubjectController {
    @Autowired
    private EduSubjectService subjectService;

    //添加课程分类
    //获取上传过来的文件，把内容读取出来
    @ApiOperation(value = "添加课程分类")
    @PostMapping("sddSubject")
    public R addSubject(MultipartFile file) {
        subjectService.saveSubject(file,subjectService);
        return R.ok();
   }

    @ApiOperation(value = "课程分类列表（树型）")
    @GetMapping("getAllSubject")
    public R getAllSubject(){
        //泛型是一级分类即可，因为里面包含二级分类
        List<OneSubject> list=subjectService.getAllOneTwoSubject();
        return R.ok().data("list",list);

   }
}

