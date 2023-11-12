var socket = null;
var userId = null;
var opponentUserId = null; // 对战中对面用户的id
var questionList = null;
var currentQuestionIndex = 0;  // 当前问题的索引
var selectedAnswerIndex = null;  // 用户每一题所选择的答案
var opponentSelectedAnswerIndex = null;  // 对战中对方选择每一题的答案
var rightAnswerIndex = 0;
var userScore = 0; // 当前用户的分数
var opponentScore = 0; // 对面对战用户的分数
var selfUsername = null;
var opponentUsername = null;

var isUserSelect = false;
var isOpponentSelect = false; // 判断对面的用户是否有选择过

// TODO 新增属性记录 对方玩家选了什么 （无论正确还是错误）同样是丰富userMatchInfo类

//强制关闭浏览器  调用websocket.close（）,进行正常关闭
window.onunload = function () {
    disconnect()
}

function connect() {
    // userId = $("#userIdInput").val();
    var socketUrl = "ws://127.0.0.1:5003/game/match/" + userId;
    socket = new WebSocket(socketUrl);
    // 在此触发OnOpen
    //打开事件
    socket.onopen = function () {
        console.log("websocket 已打开 userId: " + userId);
    };
    // 在此触发OnMessage
    //获得消息事件
    socket.onmessage = function (msg) {
        // 此处编写得到消息之后的具体逻辑
        // 提交每一题之后 需要更新玩家的分数和
        // chatMessage就是真正的类 响应数据
        let chatMessage = msg.data.chatMessage;
        // 解释
        let desc = msg.data.desc;
        // 状态码
        let code = msg.data.code;
        let type = chatMessage.type;
        // 以上均为通用代码

        // 如果接收到的是匹配时 代表此时已经有用户匹配成功 后端发送题目以及 对应的用户信息
        if (type === "MATCH_USER") {
            let gameMatchInfo = chatMessage.data;
            // gameMatchInfo对应chatMessage中的T data

            let questions = gameMatchInfo.questions;
            // 获取到所有问题的题目及答案
            questionList = JSON.parse(questions);
            // questionList表示此次对战中的题库

            // 同时需要获取到对面的用户的信息
            let selfInfo = gameMatchInfo.selfInfo;
            // selfInfo是自己的信息
            // selfInfo则代表的是GameMatchInfo中selfInfo的属性
            userId = selfInfo.userId;
            let opponentInfo = gameMatchInfo.opponentInfo;
            // opponentInfo则是对面的信息
            // 获取到对面用户的id
            opponentUserId = opponentInfo.userId;

            // 获取到对战双方的名字
            selfUsername = gameMatchInfo.selfUsername;
            opponentUsername = gameMatchInfo.opponentUsername;
        }


        // TODO 这里的type也需要增加 （枚举类中增加） 此状态表示对面提交答案了
        // 根据当前玩家是否提交 如果当前玩家也提交了的话 渲染对面选择
        // 如果当前用户未提交 等待直至提交了 再渲染

        if (type === "OPPONENT_COMMIT_ANSWER") {

            isOpponentSelect = true;
        }


        // 如果接收到的是 对战中的信息 代表此时已经对面已经回答 如果答对了需要更新分数信息
        if (type === "PLAY_GAME") {
            let data = chatMessage.data;
            // data.userId
            // 这里还有一个userId的属性 这个属性指的是对面的用户id
            // 更新对面用户的分数
            opponentScore = data.score;
            isOpponentSelect = true;
            // 更新完分数了 这时候需要重新渲染出下一题 依次进行循环
        }
        var serverMsg = "收到服务端信息: " + msg.data;
        console.log(serverMsg);
    };
    // 在此触发OnClose
    //关闭事件
    socket.onclose = function () {
        console.log("websocket 已关闭 userId: " + userId);
    };
    // 在此触发OnError
    //发生了错误事件
    socket.onerror = function () {
        console.log("websocket 发生了错误 userId : " + userId);
    }
}

function disconnect() {
    socket.close();
}

// 用户加入
function addUser() {
    var chatMessage = {};
    var sender = userId;
    var type = "ADD_USER";
    chatMessage.sender = sender;
    chatMessage.type = type;
    console.log("用户:" + sender + "开始加入......");
    socket.send(JSON.stringify(chatMessage));
}

// 随机匹配
function matchUser() {
    var chatMessage = {};
    var sender = userId;
    var type = "MATCH_USER";
    chatMessage.sender = sender;
    chatMessage.type = type;
    console.log("用户:" + sender + "开始匹配......");
    socket.send(JSON.stringify(chatMessage));
}

// 取消匹配
function cancelMatch() {
    var chatMessage = {};
    var sender = userId;
    var type = "CANCEL_MATCH";
    chatMessage.sender = sender;
    chatMessage.type = type;
    console.log("用户:" + sender + "取消匹配......");
    socket.send(JSON.stringify(chatMessage));
}

// 开始游戏
// 默认是开始的时候渲染出列表中第一项
function startGame() {
    displayQuestion(questionList[currentQuestionIndex])
    currentQuestionIndex++;
    // 初始化积分
    userScore = 0;
    opponentScore = 0
}

// 展示题目
// 除第一次打开时 选择渲染使用的方法
function displayQuestion() {
    if (currentQuestionIndex <= questionList.size)
        displayQuestion(questionList[currentQuestionIndex])
    currentQuestionIndex++;
}

// 假设你有一个函数用于显示问题
// 这个的形参指的是每一个Question对象
function displayQuestion(question) {
    var questionContainer = document.getElementById("questionContainer");

    // 清空容器
    questionContainer.innerHTML = '';

    // 渲染出对应的问题 (题干)
    var questionElement = document.createElement('p');
    questionElement.textContent = question.questionMsg;
    questionContainer.appendChild(questionElement);

    // 渲染题目选项
    // TODO 随机将正确答案和错误答案顺序放入options中 随机之后 确认一个标识 这个标识指的是对的那一项的索引
    rightAnswerIndex = 0;
    // 目前先写死
    var options = [question.rightAnswer, question.wrongAnswer1, question.wrongAnswer2, question.wrongAnswer3];

    var index = 0
    // index为每一个选项自己所独特的id 与数组中的索引相对应
    // 如上可以通过这种方法判断出 当 rightAnswerIndex == index 的时候 玩家就答对了

    // 遍历选项数组，为每个选项创建单选按钮
    options.forEach(function (option) {
        var label = document.createElement('label');
        var radio = document.createElement('input');
        radio.setAttribute("data-index", index);
        index = index + 1;
        radio.type = 'radio';
        radio.name = 'answer';
        radio.value = option;
// 添加事件监听器，当选择变化时更新变量
        radio.addEventListener('change', function () {
            selectedAnswerIndex = radio.getAttribute("data-index");
        });
        index++;
        label.appendChild(radio);
        label.appendChild(document.createTextNode(option));
        questionContainer.appendChild(label);
    });
}

// 游戏中
// function userInPlay(){
// 这里的question是对应的问题的编号
function commitAnswer(question) {
    isUserSelect = true;
    // 这里标记当前用户已经选择了
    var chatMessage = {};
    var sender = userId;
    var type = "PLAY_GAME";
    let isRight = checkAnswer(question);
    // 如果答对了就更新分数 客户端向服务器发起请求
    // 如果没答对 也发送请求 此时也会执行代码 但是分数不会有变化
    if (isRight)
        userScore += 50;
    // userScore是玩家的积分 TODO 根据玩家所耗时间来决定积分的加多少
    var data = userScore;
    chatMessage.sender = sender;
    chatMessage.data = data;
    chatMessage.type = type;
    // console.log("用户:" + sender + "更新分数为" + data);
    console.log("用户:" + sender + "更新分数为" + data);
    socket.send(JSON.stringify(chatMessage));

}

// 判断题目提交的答案是否正确
function checkAnswer(question) {
    // 如果他们两个相等的话 就代表是答对的
    if (rightAnswerIndex === selectedAnswerIndex)
        return true;
    return false;
}

// 提交完一题的答案 知道对面和自己的选择之后 需要切换到下一题 即调用displayQuestion函数 在上面


// 按照游戏流程 写完所有的题之后 需要到结算页面
// 游戏结束
// 结束的时候 要告诉服务器谁是获胜者 把获胜者的id传过去吧
function gameover() {
    var chatMessage = {};
    var data = null;
    // 看谁分高 谁分高谁就赢了
    if (userScore > opponentScore)
        data = userId;
    else if (opponentScore > userScore)
        data = opponentScore;
    var sender = userId;
    var type = "GAME_OVER";
    chatMessage.sender = sender;
    chatMessage.type = type;
    chatMessage.data = data;
    console.log("用户:" + sender + "结束游戏");
    socket.send(JSON.stringify(chatMessage));
    userScore = 0;
    opponentScore = 0;
}