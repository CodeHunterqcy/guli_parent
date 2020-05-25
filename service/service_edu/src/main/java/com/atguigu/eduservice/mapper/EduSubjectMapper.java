package com.atguigu.eduservice.mapper;

import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.subject.OneSubject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 课程科目 Mapper 接口
 * </p>
 *
 * @author CodeHunter_qcy
 * @since 2020-05-19
 */
public interface EduSubjectMapper extends BaseMapper<EduSubject> {

    List<EduSubject> selectList(QueryWrapper<OneSubject> wrapperOne);
}
