package com.gduf.ws.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gduf.pojo.user.UserWithValue;
import com.gduf.pojo.wikipedia.Question;
import com.gduf.service.CompetitionService;
import com.gduf.service.QuestionService;
import com.gduf.service.UserService;
import com.gduf.ws.entity.*;
import com.gduf.ws.error.GameServerError;
import com.gduf.ws.exception.GameServerException;
import com.gduf.ws.utils.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.gduf.controller.Code.MATCH_TASK_NAME_PREFIX;

@Component
@Slf4j
//@ServerEndpoint(value = "/competition")
@ServerEndpoint(value = "/competition/{userId}")
public class ChatWebsocket {

    private Session session;

    private String userId;

    static MatchCacheUtil matchCacheUtil;

    static Lock lock = new ReentrantLock();

    static Condition matchCond = lock.newCondition();

    static QuestionService questionService;

    static CompetitionService competitionService;

    static UserService userService;

    static AnswerSituationUtil answerSituationUtil;

    @Autowired
    public void setQuestionService(QuestionService questionService) {
        ChatWebsocket.questionService = questionService;
    }

    @Autowired
    public void setQuestionService(CompetitionService competitionService) {
        ChatWebsocket.competitionService = competitionService;
    }

    @Autowired
    public void setQuestionService(UserService userService) {
        ChatWebsocket.userService = userService;
    }

    @Autowired
    public void setMatchCacheUtil(MatchCacheUtil matchCacheUtil) {
        ChatWebsocket.matchCacheUtil = matchCacheUtil;
    }

    @Autowired
    public void setAnswerSituationUtil(AnswerSituationUtil answerSituationUtil) {
        ChatWebsocket.answerSituationUtil = answerSituationUtil;
    }

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        System.out.println(session);
        log.info("ChatWebsocket open 有新连接加入 userId: {}", userId);
        this.userId = userId;
        this.session = session;
        matchCacheUtil.addClient(userId, this);

        log.info("ChatWebsocket open 连接建立完成 userId: {}", userId);
    }

    @OnError
    public void onError(Session session, Throwable error) {

        log.error("ChatWebsocket onError 发生了错误 userId: {}, errorMessage: {}", userId, error.getMessage());

        matchCacheUtil.removeClinet(userId);
        matchCacheUtil.removeUserOnlineStatus(userId);
        matchCacheUtil.removeUserFromRoom(userId);
        matchCacheUtil.removeUserMatchInfo(userId);

        log.info("ChatWebsocket onError 连接断开完成 userId: {}", userId);
    }

    @OnClose
    public void onClose() {
        log.info("ChatWebsocket onClose 连接断开 userId: {}", userId);

        matchCacheUtil.removeClinet(userId);
        matchCacheUtil.removeUserOnlineStatus(userId);
        matchCacheUtil.removeUserFromRoom(userId);
        matchCacheUtil.removeUserMatchInfo(userId);

        log.info("ChatWebsocket onClose 连接断开完成 userId: {}", userId);
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        log.info("ChatWebsocket onMessage userId: {}, 来自客户端的消息 message: {}", userId, message);

        JSONObject jsonObject = JSON.parseObject(message);
        MessageTypeEnum type = jsonObject.getObject("type", MessageTypeEnum.class);

        log.info("ChatWebsocket onMessage userId: {}, 来自客户端的消息类型 type: {}", userId, type);

        if (type == MessageTypeEnum.ADD_USER) {
            addUser(jsonObject);
        } else if (type == MessageTypeEnum.MATCH_USER) {
            matchUser(jsonObject);
        } else if (type == MessageTypeEnum.CANCEL_MATCH) {
            cancelMatch(jsonObject);
        } else if (type == MessageTypeEnum.PLAY_GAME) {
            toPlay(jsonObject);
        } else if (type == MessageTypeEnum.GAME_OVER) {
            gameover(jsonObject);
        } else {
            throw new GameServerException(GameServerError.WEBSOCKET_ADD_USER_FAILED);
        }

        log.info("ChatWebsocket onMessage userId: {} 消息接收结束", userId);
    }

    /**
     * 群发消息
     */
    private void sendMessageAll(MessageReply<?> messageReply) {

        log.info("ChatWebsocket sendMessageAll 消息群发开始 userId: {}, messageReply: {}", userId, JSON.toJSONString(messageReply));

        Set<String> receivers = messageReply.getChatMessage().getReceivers();
        for (String receiver : receivers) {
            ChatWebsocket client = matchCacheUtil.getClient(receiver);
            client.session.getAsyncRemote().sendText(JSON.toJSONString(messageReply));
        }

        log.info("ChatWebsocket sendMessageAll 消息群发结束 userId: {}", userId);
    }

    /**
     * 用户加入游戏
     */
    private void addUser(JSONObject jsonObject) {
//        加入他人的房间的时候
        log.info("ChatWebsocket addUser 用户加入游戏开始 message: {}, userId: {}", jsonObject.toJSONString(), userId);

        MessageReply<Object> messageReply = new MessageReply<>();
        ChatMessage<Object> result = new ChatMessage<>();
        result.setType(MessageTypeEnum.ADD_USER);
        result.setSender(userId);

        /*
         * 获取用户的在线状态
         * 如果缓存中没有保存用户状态，表示用户新加入，则设置为在线状态
         * 否则直接返回
         */
        StatusEnum status = matchCacheUtil.getUserOnlineStatus(userId);
        if (status != null) {
            /*
             * 想要进入加入游戏只会有两种状态 第一种是 刚gameover完 重开一把
             * 另一种是从外面的世界进来 即刚触发OnOpen的
             * 所以如果为空 就创建此 放入待匹配的状态
             * 如果为游戏结束状态，重新设置为在线状态 放入待匹配状态
             * 否则返回错误提示信息
             */
            if (status.compareTo(StatusEnum.GAME_OVER) == 0) {
                messageReply.setCode(MessageCode.SUCCESS.getCode());
                messageReply.setDesc(MessageCode.SUCCESS.getDesc());
                matchCacheUtil.setUserIDLE(userId);
            } else {
//                返回错误信息 也不算错误吧只能说是警告
                messageReply.setCode(MessageCode.USER_IS_ONLINE.getCode());
                messageReply.setDesc(MessageCode.USER_IS_ONLINE.getDesc());
            }
        } else {
            messageReply.setCode(MessageCode.SUCCESS.getCode());
            messageReply.setDesc(MessageCode.SUCCESS.getDesc());
            matchCacheUtil.setUserIDLE(userId);
        }

        Set<String> receivers = new HashSet<>();
        receivers.add(userId);
        result.setReceivers(receivers);
        messageReply.setChatMessage(result);

        sendMessageAll(messageReply);

        log.info("ChatWebsocket addUser 用户加入游戏结束 message: {}, userId: {}", jsonObject.toJSONString(), userId);

    }

    /**
     * 用户随机匹配对手
     */
    @SneakyThrows
    private void matchUser(JSONObject jsonObject) {

        log.info("ChatWebsocket matchUser 用户随机匹配对手开始 message: {}, userId: {}", jsonObject.toJSONString(), userId);

        MessageReply<GameMatchInfo> messageReply = new MessageReply<>();
        ChatMessage<GameMatchInfo> result = new ChatMessage<>();
        result.setSender(userId);
        result.setType(MessageTypeEnum.MATCH_USER);

        lock.lock();
        try {
            // 设置用户状态为匹配中
//            TODO 修改工具类的类型 使他不只能存储id 也能存储玩家的段位
            matchCacheUtil.setUserInMatch(userId);
            matchCond.signal();
        } finally {
            lock.unlock();
        }

        // 创建一个异步线程任务，负责匹配其他同样处于匹配状态的其他用户
        Thread matchThread = new Thread(() -> {
            boolean flag = true;
            String receiver = null;
            while (flag) {
                // 获取除自己以外的其他待匹配用户
                lock.lock();
                try {
                    // 当前用户不处于待匹配状态 直接返回
                    if (matchCacheUtil.getUserOnlineStatus(userId).compareTo(StatusEnum.IN_GAME) == 0
//                            观察当前用户是否游戏中状态
                            || matchCacheUtil.getUserOnlineStatus(userId).compareTo(StatusEnum.GAME_OVER) == 0) {
//                        观察当前用户是否游戏结束 结算的状态
                        log.info("ChatWebsocket matchUser 当前用户 {} 已退出匹配", userId);
                        return;
                    }
                    // 当前用户取消匹配状态
                    if (matchCacheUtil.getUserOnlineStatus(userId).compareTo(StatusEnum.IDLE) == 0) {
                        // 当前用户取消匹配
                        messageReply.setCode(MessageCode.CANCEL_MATCH_ERROR.getCode());
                        messageReply.setDesc(MessageCode.CANCEL_MATCH_ERROR.getDesc());
//                        设定返回信息 将从匹配中的状态该为待匹配
                        Set<String> set = new HashSet<>();
                        set.add(userId);
                        result.setReceivers(set);
                        result.setType(MessageTypeEnum.CANCEL_MATCH);
                        messageReply.setChatMessage(result);
                        log.info("ChatWebsocket matchUser 当前用户 {} 已退出匹配", userId);
//                        发送返回信息
                        sendMessageAll(messageReply);
                        return;
                    }

//                    从room中获取对手的对象 这个receiver是对手的id
//                    可以在这里下手脚 加一个while循环 判断直至找到相同段位的用户为止
//                    TODO 直接在setUserMatchRoom那块 加入玩家的段位信息 需要改动工具类
                    String userDan = competitionService.showPlayersExtraDan(Integer.parseInt(userId));
                    while (true) {
                        receiver = matchCacheUtil.getUserInMatchRandom(userId);
//                        这里必须把判空放在上面 否则如果是队列中没有人在匹配 却给receiver调用了Integer.parseInt(receiver)方法 会报空指针
                        if (Objects.isNull(receiver))
                            break;
                        else if (userDan.equals(competitionService.showPlayersExtraDan(Integer.parseInt(receiver))))
                            break;
                    }
                    if (receiver != null) {
                        // 对手不处于待匹配状态
                        if (matchCacheUtil.getUserOnlineStatus(receiver).compareTo(StatusEnum.IN_MATCH) != 0) {
                            log.info("ChatWebsocket matchUser 当前用户 {}, 匹配对手 {} 已退出匹配状态", userId, receiver);
                        } else {
//                            进了这个else就表明用户已经匹配到状态正常的对手了
//                            设定对手的基本信息
                            matchCacheUtil.setUserInGame(userId);
                            matchCacheUtil.setUserInGame(receiver);
//                            将对手放入房间中 （指定唯一user）
                            matchCacheUtil.setUserInRoom(userId, receiver);
                            flag = false;
//                            匹配到了 令flag为false 跳出while循环
//                            此次匹配结束 进入开打状态
                        }
                    } else {
                        // 如果当前没有待匹配用户，进入等待队列
                        try {
                            log.info("ChatWebsocket matchUser 当前用户 {} 无对手可匹配", userId);
                            matchCond.await();
                        } catch (InterruptedException e) {
                            log.error("ChatWebsocket matchUser 匹配线程 {} 发生异常: {}",
                                    Thread.currentThread().getName(), e.getMessage());
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }

            log.info("已找到玩家 双方分别为" + userId + "和" + receiver);

            UserMatchInfo senderInfo = new UserMatchInfo();
            UserMatchInfo receiverInfo = new UserMatchInfo();
            senderInfo.setUserId(userId);
            senderInfo.setScore(0);
            receiverInfo.setUserId(receiver);
            receiverInfo.setScore(0);
//            两个对象分别记录两个玩家的得分

            matchCacheUtil.setUserMatchInfo(userId, JSON.toJSONString(senderInfo));
            matchCacheUtil.setUserMatchInfo(receiver, JSON.toJSONString(receiverInfo));
//            初始化接下来pk中玩家的信息 (每一道题提交答案完成都会 刷新一次对应的得分)

            GameMatchInfo gameMatchInfo = new GameMatchInfo();

//            获取玩家段位 根据玩家段位来获取题目
            String dan = competitionService.showPlayersDan(Integer.parseInt(userId));
            List<Question> questions = questionService.getCompetitionQuestionsByDan(dan);

            gameMatchInfo.setQuestions(questions);
            gameMatchInfo.setSelfInfo(senderInfo);
            gameMatchInfo.setOpponentInfo(receiverInfo);
//            一次性获取所有题目
//            存入此次对战中的当前玩家 对方 题目
//            一个GameMatchInfo就代表一个玩家的对象

//            新增 存入此次pk中对方的用户名 头像
            UserWithValue userWithValue = userService.showUser(Integer.parseInt(userId));
            String username = userWithValue.getUser().getUsername();
            String userPic = userWithValue.getUserValue().getPic();
            gameMatchInfo.setSelfUsername(username);
            gameMatchInfo.setSelfPicAvatar(userPic);

            UserWithValue receiverValue = userService.showUser(Integer.parseInt(receiver));
            String receiverUsername = receiverValue.getUser().getUsername();
            String opponentPic = receiverValue.getUserValue().getPic();
            gameMatchInfo.setOpponentUsername(receiverUsername);
            gameMatchInfo.setOpponentPicAvatar(opponentPic);

            messageReply.setCode(MessageCode.SUCCESS.getCode());
            messageReply.setDesc(MessageCode.SUCCESS.getDesc());
//            确认返回信息的类型以及数据

            result.setData(gameMatchInfo);
            Set<String> set = new HashSet<>();
            set.add(userId);
            result.setReceivers(set);
            result.setType(MessageTypeEnum.MATCH_USER);
            messageReply.setChatMessage(result);
            sendMessageAll(messageReply);

//            自己的传给自己的 对面的传给对面的
            gameMatchInfo.setSelfInfo(senderInfo);
            gameMatchInfo.setOpponentInfo(receiverInfo);

            result.setData(gameMatchInfo);
            set.clear();
            set.add(receiver);
            result.setReceivers(set);
            messageReply.setChatMessage(result);

            sendMessageAll(messageReply);

            log.info("ChatWebsocket matchUser 用户随机匹配对手结束 messageReply: {}", JSON.toJSONString(messageReply));

        }, MATCH_TASK_NAME_PREFIX + userId);
        matchThread.start();
    }

    /**
     * 取消匹配
     */
    private void cancelMatch(JSONObject jsonObject) {

        log.info("ChatWebsocket cancelMatch 用户取消匹配开始 userId: {}, message: {}", userId, jsonObject.toJSONString());

        lock.lock();
        try {
//            IDLE为待匹配状态
            matchCacheUtil.setUserIDLE(userId);
        } finally {
            lock.unlock();
        }

        log.info("ChatWebsocket cancelMatch 用户取消匹配结束 userId: {}", userId);
    }

    /**
     * 游戏中
     */
    @SneakyThrows
    public void toPlay(JSONObject jsonObject) {
//        每一道题提交了都会重新执行一次这个方法
//        (由答题方 执行)
        log.info("ChatWebsocket toPlay 用户更新对局信息开始 userId: {}, message: {}", userId, jsonObject.toJSONString());

//        MessageReply<UserMatchInfo> messageReply = new MessageReply<>();
        MessageReply<ScoreSelectedInfo> messageReply = new MessageReply<>();
//        返回的信息类型

//        下面的这个思路就是 从房间中找到对方 并且发送自己的分数更新信息给他
//        ChatMessage<UserMatchInfo> result = new ChatMessage<>();
        ChatMessage<ScoreSelectedInfo> result = new ChatMessage<>();
        result.setSender(userId);
        String receiver = matchCacheUtil.getUserFromRoom(userId);
//        从房间中找出对面的对手是谁 发信息给他
        Set<String> set = new HashSet<>();
        set.add(receiver);
        result.setReceivers(set);
        result.setType(MessageTypeEnum.PLAY_GAME);
//        设置消息的发送方 和 接收方 以及消息类型 (游戏中)

//        获取新的得分 并且重新赋值给当前的user   (当前的user就是得分的那个)
        UserMatchChoice userMatchChoice = jsonObject.getObject("data", UserMatchChoice.class);
        Integer newScore = userMatchChoice.getUserScore();
        String userSelectedAnswer = userMatchChoice.getUserSelectedAnswer();

//        获取answerSituation对象 此对象中是所有正在游戏中的用户的回答信息 暂时存在这里
//        TODO 迁移到redis中
        answerSituationUtil.addAnswer(userId, userSelectedAnswer);

        UserMatchInfo userMatchInfo = new UserMatchInfo();
        userMatchInfo.setUserId(userId);
        userMatchInfo.setScore(newScore);

//        setUserMatchInfo所改变的数据是 同一时刻所有对战的用户的信息
//        在这里set是根据当前用户的id
//        重新设置一下对应的用户对战信息
        matchCacheUtil.setUserMatchInfo(userId, JSON.toJSONString(userMatchInfo));

//        设置响应数据的类型
//        更新 同时发送对面所选的选项
        result.setData(new ScoreSelectedInfo(userMatchInfo, userSelectedAnswer));
//        result.setData(userMatchInfo);
        messageReply.setCode(MessageCode.SUCCESS.getCode());
        messageReply.setDesc(MessageCode.SUCCESS.getDesc());
        messageReply.setChatMessage(result);

//        返回包含当前用户的新信息的响应数据
        sendMessageAll(messageReply);

        log.info("ChatWebsocket toPlay 用户更新对局信息结束 userId: {}, userMatchInfo: {}", userId, JSON.toJSONString(userMatchInfo));
    }

    /**
     * 游戏结束
     */
    public void gameover(JSONObject jsonObject) {

        log.info("ChatWebsocket gameover 用户对局结束 userId: {}, message: {}", userId, jsonObject.toJSONString());

//        设置响应数据类型
        MessageReply<RoomAnswerSituation> messageReply = new MessageReply<>();

//        设置响应数据 改变玩家的状态
        ChatMessage<RoomAnswerSituation> result = new ChatMessage<>();
        result.setSender(userId);
        String receiver = matchCacheUtil.getUserFromRoom(userId);
        result.setType(MessageTypeEnum.GAME_OVER);
        lock.lock();
        try {
//            设定用户为游戏结束的状态
            matchCacheUtil.setUserGameover(userId);
            if (matchCacheUtil.getUserOnlineStatus(receiver).compareTo(StatusEnum.GAME_OVER) == 0) {
                messageReply.setCode(MessageCode.SUCCESS.getCode());
                messageReply.setDesc(MessageCode.SUCCESS.getDesc());

                //        记录赢了的玩家的ID
                Integer winnerId = jsonObject.getInteger("data");
                boolean isUpdate = competitionService.updateUserStar(winnerId);
                if (!isUpdate) {
                    messageReply.setCode(MessageCode.SYSTEM_ERROR.getCode());
                    messageReply.setDesc(MessageCode.SYSTEM_ERROR.getDesc());
                }

//                获取对战后的对战信息
                AnswerSituation selfAnswer = answerSituationUtil.getAnswer(userId);
                AnswerSituation opponentAnswer = answerSituationUtil.getAnswer(receiver);
                RoomAnswerSituation roomAnswerSituation = new RoomAnswerSituation(userId, receiver, selfAnswer, opponentAnswer);
                result.setData(roomAnswerSituation);

//                设置完结后的返回信息
                messageReply.setChatMessage(result);
                Set<String> set = new HashSet<>();
                set.add(receiver);
                result.setReceivers(set);
                sendMessageAll(messageReply);
//                屎山会出手 两边全部发
                set.clear();
                set.add(userId);
                result.setReceivers(set);
                sendMessageAll(messageReply);

//                移除属于游戏中的游戏信息
                matchCacheUtil.removeUserMatchInfo(userId);
                matchCacheUtil.removeUserFromRoom(userId);

//                移除属于这一次的游戏选择信息
                answerSituationUtil.removeAnswer(userId);
                answerSituationUtil.removeAnswer(receiver);
            }
        } finally {
            lock.unlock();
        }

        log.info("ChatWebsocket gameover 对局 [{} - {}] 结束", userId, receiver);
    }
}
