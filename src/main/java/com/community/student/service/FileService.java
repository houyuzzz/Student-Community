package com.community.student.service;

import com.community.student.dao.SchoolNoticeMapper;
import com.community.student.entity.Message;
import com.community.student.entity.SchoolFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    @Autowired
    SchoolNoticeMapper schoolNoticeMapper;

    public List<SchoolFile> findSchoolFiles() {
        return schoolNoticeMapper.selectSchoolFiles();
    }

    public int findSchoolFilesCount() {
        return schoolNoticeMapper.selectFileCount();
    }

    public int insertSchoolFiles(SchoolFile schoolFile) {
        return schoolNoticeMapper.insertSchoolFile(schoolFile);
    }
}
