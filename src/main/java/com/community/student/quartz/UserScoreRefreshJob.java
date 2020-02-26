package com.community.student.quartz;

import com.community.student.entity.DiscussPost;
import com.community.student.entity.User;
import com.community.student.service.*;
import com.community.student.util.CommunityConstant;
import com.community.student.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class UserScoreRefreshJob implements Job, CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserScoreRefreshJob.class);


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


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("[任务结束] 用户分数刷新开始!");
        List<User> users = userService.findAllUser(0,userService.findAllUserCount());
        for (User user :users) {
            refresh(user.getId());
        }
        logger.info("[任务结束] 用户分数刷新完毕!");
    }

    private void refresh(int userId) {

        int disscustPostCount = discussPostService.findDiscussPostRows(userId);

        int likeCount = likeService.findUserLikeCount(userId);

        int commentCount = commentService.findCommentCount(userId);

        int followerCount = (int)followService.findFolloweeCount(userId, ENTITY_TYPE_USER);

        int score = disscustPostCount*1+likeCount*5+commentCount*1+followerCount*10;

        userService.updateScore(userId,score);
    }

}
