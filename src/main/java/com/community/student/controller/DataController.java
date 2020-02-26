package com.community.student.controller;

import com.community.student.entity.Page;
import com.community.student.entity.User;
import com.community.student.service.*;
import com.community.student.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@Controller
public class DataController implements CommunityConstant {

    @Autowired
    private DataService dataService;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    // 统计页面
    @RequestMapping(path = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage() {
        return "/site/admin/data";
    }

    // 统计页面
    @RequestMapping(path = "/data/student", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataStudentPage(Page page, Model model) {


        page.setLimit(5);
        page.setPath("/data/student");
        page.setRows(userService.findAllUserCount());
        List<User> users = userService.findAllUser(page.getOffset(),page.getLimit());


        List<Map<String, Object>> userVOList = new ArrayList<>();
        for (User user :users) {
            Map<String, Object> map = new HashMap<>();
            map.put("user",user);

            int disscustPostCount = discussPostService.findDiscussPostRows(user.getId());
            map.put("disscustpost",disscustPostCount);

            int likeCount = likeService.findUserLikeCount(user.getId());
            map.put("like",likeCount);

            int commentCount = commentService.findCommentCount(user.getId());
            map.put("comment",commentCount);

            int followerCount = (int)followService.findFolloweeCount(user.getId(), ENTITY_TYPE_USER);
            map.put("follow",followerCount);

            int score = user.getScore();
            map.put("score",score);
            userVOList.add(map);
        }


        model.addAttribute("users", userVOList);
        return "/site/admin/studentdata";
    }

    // 统计网站UV
    @RequestMapping(path = "/data/uv", method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult", uv);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);
        return "forward:/data";
    }

    // 统计活跃用户
    @RequestMapping(path = "/data/dau", method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult", dau);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);
        return "forward:/data";
    }

}
