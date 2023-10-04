package com.itheima.service;

import com.itheima.dao.UserDAO;
import com.itheima.domain.Book;
import com.itheima.pojo.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private BookService bookService;
    @Autowired
    private UserDAO userDAO;
    
    @Test
    public void testGetById() {
        Book book = bookService.getById(2);
        System.out.println(book);
    }

    @Test
    public void testGetAll() {
        List<Book> all = bookService.getAll();
        System.out.println(all);
    }

    @Test
    public void testInsert() {
        userDAO.insertBasic(new User("1", "1"));
    }

    @Test
    public void testLogin(){
        System.out.println(userDAO.getByUsername("3"));
    }

    @Test
    public void testNewsController(){

    }

}
