package com.community.student.dao;

import com.community.student.entity.SchoolFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface SchoolNoticeMapper {

    int insertSchoolFile(SchoolFile schoolFile);
    // 查询数量
    int selectFileCount();
    // 查询当前列表
    int deleteFile(int id);
    List<SchoolFile> selectSchoolFiles();
}
