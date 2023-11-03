package com.gduf.util;

import com.gduf.pojo.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类
 */
public class JwtUtil {

//    @Autowired
    private static RedisCache redisCache;

    @Autowired
    public JwtUtil(RedisCache redisCache){
        this.redisCache = redisCache;
    }

    //有效期为
    public static final Long JWT_TTL = 60 * 60 *1000L;// 60 * 60 *1000  一个小时
    //设置秘钥明文
    public static final String JWT_KEY = "czc";

    public static String getUUID(){
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }

    /**
     * 生成jtw
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());// 设置过期时间
        return builder.compact();
    }

    /**
     * 生成jtw
     * @param subject token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());// 设置过期时间
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if(ttlMillis==null){
            ttlMillis=JwtUtil.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
//        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer("sg")     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey); //使用HS256对称加密算法签名, 第二个参数为秘钥
//                .setExpiration(expDate);
    }

    /**
     * 创建token
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);// 设置过期时间
        return builder.compact();
    }

    public static void main(String[] args) throws Exception {
//        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjYWM2ZDVhZi1mNjVlLTQ0MDAtYjcxMi0zYWEwOGIyOTIwYjQiLCJzdWIiOiJzZyIsImlzcyI6InNnIiwiaWF0IjoxNjM4MTA2NzEyLCJleHAiOjE2MzgxMTAzMTJ9.JVsSbkP94wuczb4QryQbAke3ysBDIL5ou8fWsbt_ebg";
//        Claims claims = parseJWT(token);
//        System.out.println(claims);
//        String jwt = createJWT("0");
//        System.out.println(jwt);
//        Claims x = parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhMzJkYTM2ZTYxY2I0MjJiOTc3NmY5ODJmNTk5Njg4ZCIsInN1YiI6IjAiLCJpc3MiOiJzZyIsImlhdCI6MTY5NzYzODg1OH0.5-zD7hDvC-iCWyqMyNMlmdF8XTkBx8HvuQ8NtyUD5F8");
//        System.out.println(x.getSubject());
//        System.out.println(parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhMzJkYTM2ZTYxY2I0MjJiOTc3NmY5ODJmNTk5Njg4ZCIsInN1YiI6IjAiLCJpc3MiOiJzZyIsImlhdCI6MTY5NzYzODg1OH0.5-zD7hDvC-iCWyqMyNMlmdF8XTkBx8HvuQ8NtyUD5F8"));
//        System.out.println(createJWT("2"));
//        System.out.println(parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3MzFmMWIyZjgxMTA0NjAwODgyZTNkOGNiZTg5NzM2MCIsInN1YiI6IjIiLCJpc3MiOiJzZyIsImlhdCI6MTY5ODI5NzMxM30.OuRRp77ZEvq1pKrHMQWrBxrwIySmvVXCH8Iah101fy4"));
//        System.out.println(parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0MTQyNTg1OTU2YzU0YzFjYmI0YzY4YWJhNjdiY2ExOCIsInN1YiI6IjIiLCJpc3MiOiJzZyIsImlhdCI6MTY5ODMxODI5NX0.9MXAGV42SWYKN6iJ4oNzmaGVsMKbmg3KF05qandUn64"));
        System.out.println(parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhMzJkYTM2ZTYxY2I0MjJiOTc3NmY5ODJmNTk5Njg4ZCIsInN1YiI6IjAiLCJpc3MiOiJzZyIsImlhdCI6MTY5NzYzODg1OH0.5-zD7hDvC-iCWyqMyNMlmdF8XTkBx8HvuQ8NtyUD5F8"));
    }

    /**
     * 生成加密后的秘钥 secretKey
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 解析
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }


    public  User decode(String token) throws Exception {
        Claims claims = JwtUtil.parseJWT(token);
        String userId = claims.getSubject();
//            getSubject获取的是未加密之前的原始值
        User user = redisCache.getCacheObject("login0");
//        User user = redisCache.getCacheObject("login" + userId);
        return user;
    }


}