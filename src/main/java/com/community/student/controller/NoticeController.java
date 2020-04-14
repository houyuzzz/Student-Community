package com.community.student.controller;

import com.community.student.annotation.LoginRequired;
import com.community.student.entity.SchoolFile;
import com.community.student.entity.User;
import com.community.student.service.UserService;
import com.community.student.service.FileService;
import com.community.student.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Controller
public class NoticeController {
    private static final Logger logger = LoggerFactory.getLogger(NoticeController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.upload}")
    private String uploadPath;

    @RequestMapping(path = "/schoolNotice", method = RequestMethod.GET)
    public String getSchoolPage(Model model) {
////        page.setRows(fileService.findSchoolFilesCount() == 0 ? 0 : fileService.findSchoolFilesCount());
//        page.setPath("/schoolNotice" );

        List<SchoolFile> list = fileService.findSchoolFiles();
        List<Map<String, Object>> files = new ArrayList<>();
        if (list != null) {
            for (SchoolFile file : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("file", file);
                User user = userService.findUserById(file.getUserId());
                map.put("user", user);
                files.add(map);
            }
        }
        model.addAttribute("files", files);
        return "/site/schoolNotice";
    }

    //上传文件
    @LoginRequired
    @RequestMapping(path = "/noticeUpload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile file, Model model) {
        if (file == null) {
            model.addAttribute("error", "您还没有选择文件");
            return "/site/schoolNotice";
        }

        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/schoolNotice";
        }

        /*// 生成随机文件名（防止重名）
        fileName = CommunityUtil.generateUUID() + suffix;*/
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            file.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        User user = hostHolder.getUser();
        String fileUrl = domain + contextPath + "/school/" + fileName;
        // 更新文件
        SchoolFile schoolFile = new SchoolFile();
        schoolFile.setCreateTime(new Date());
        schoolFile.setUserId(user.getId());
        schoolFile.setFileName(fileName);
        schoolFile.setFileUrl(fileUrl);
        fileService.insertSchoolFiles(schoolFile);
        return "redirect:/schoolNotice";
    }

    //获得文件
    @RequestMapping(path = "/school/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;

        // 响应图片
        response.setContentType("application/x-msdownload");
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取文件失败: " + e.getMessage());
        }
    }



}
