package com.itheima.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TokenBasedAuthentication {

    private static final Map<String, String> userDatabase = new HashMap<>();
    private static final Map<String, String> tokenDatabase = new HashMap<>();
    private static final SecureRandom random = new SecureRandom();

    // 生成随机的令牌
    public static String generateToken() {
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getEncoder().encodeToString(tokenBytes);
    }

    // 注册新用户
    public static void registerUser(String username, String password) {
        userDatabase.put(username, password);
    }

    // 登录并生成令牌
    public static String login(String username, String password) {
        if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
            String token = generateToken();
            tokenDatabase.put(token, username);
            return token;
        }
        return null; // 登录失败
    }

    // 验证令牌并获取用户名
    public static String getUsernameFromToken(String token) {
        return tokenDatabase.get(token);
    }

    public static void main(String[] args) {
        // 注册新用户
        registerUser("user1", "password123");

        // 用户登录，获取令牌
        String token = login("user1", "password123");

        if (token != null) {
            // 在请求中传递令牌，模拟从客户端发送到服务器的过程
            // 在实际应用中，通常将令牌放在HTTP请求头或其他安全的方式中
            String username = getUsernameFromToken(token);
            System.out.println("登录成功，用户名：" + username + "，令牌：" + token);

            // 在后续请求中，可以使用令牌进行身份验证和授权
        } else {
            System.out.println("登录失败");
        }
    }
}
