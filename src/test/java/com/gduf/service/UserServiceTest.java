package com.gduf.service;

import com.gduf.dao.ScriptDAO;
import com.gduf.dao.UserDAO;
import com.gduf.domain.Book;
import com.gduf.pojo.script.ScriptNodeMsg;
import com.gduf.pojo.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ScriptDAO scriptDAO;
    @Autowired
    private ScriptService scriptService;

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

}
