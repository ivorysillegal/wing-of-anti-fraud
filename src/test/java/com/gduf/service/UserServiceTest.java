package com.gduf.service;

import com.gduf.dao.CommunityDAO;
import com.gduf.dao.ScriptDAO;
import com.gduf.dao.UserDAO;
import com.gduf.pojo.script.ScriptNodeMsg;
import com.gduf.pojo.user.User;
import com.gduf.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.gduf.util.ImageToBase64Converter.convertImageToBase64;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ScriptDAO scriptDAO;
    @Autowired
    private ScriptService scriptService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommunityDAO communityDAO;

    @Test
    public void testInsert() {
        userDAO.insertBasic(new User("1", "1"));
    }

    @Test
    public void testLogin() {
        System.out.println(userDAO.getByUsername("3"));
    }

    @Test
    public void testNewsController() {

    }


    @Test
    public void testScriptNode() {
        List<ScriptNodeMsg> scriptNodeMsg = scriptDAO.getScriptNodeMsg(1);
        System.out.println(scriptNodeMsg);
    }

    @Test
    public void testScript() {
        System.out.println(scriptDAO.getScriptNodeChoice(1, 1));
//        System.out.println(scriptDAO.test());
    }

    @Test
    public void test() {
        System.out.println(scriptDAO.getScriptMsg(1));
    }

    @Test
    public void testInfluence() {
        System.out.println(scriptDAO.getInfluenceName(2));
    }

    @Test
    public void testPicUpload() {
        String imagePath = "H:\\wing-of-anti-fraud\\src\\main\\resources\\captcha\\captcha.jpg"; // 指定你的图片文件路径
        String base64Image = convertImageToBase64(imagePath);
        System.out.println("Base64字符串: " + base64Image);
//        System.out.println(userDAO.updatePic(10,"2"));
//        System.out.println(userService.picUpload(new ImageUploadRequest(10, "12.jpg", "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")));
//        System.out.println(userService.picUpload(new ImageUploadRequest(10, "2.jpg", base64Image)));
    }

    @Test
    public void testCommunity() {
        System.out.println(communityDAO.showAllPost());
//        System.out.println(communityDAO.showPostByWriter(38));
//        System.out.println(communityDAO.show());
    }

    @Test
    public void testShow(){
        System.out.println(userDAO.getValueById(0));
    }


    @Test
    public void testUserValue(){
        System.out.println(userService.picUpload("111",JwtUtil.createJWT(String.valueOf(42))));
    }

    @Test
    public void testMyToken(){
        System.out.println(userService.showMyComment("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhMzJkYTM2ZTYxY2I0MjJiOTc3NmY5ODJmNTk5Njg4ZCIsInN1YiI6IjAiLCJpc3MiOiJzZyIsImlhdCI6MTY5NzYzODg1OH0.5-zD7hDvC-iCWyqMyNMlmdF8XTkBx8HvuQ8NtyUD5F8"));
    }
}
