let switchCtn = document.querySelector("#switch-cnt");
let switchC1 = document.querySelector("#switch-c1");
let switchC2 = document.querySelector("#switch-c2");
let switchCircle = document.querySelectorAll(".switch_circle");
let switchBtn = document.querySelectorAll(".switch-btn");
let loginUp = document.querySelector("#loginUp");
let loginIn = document.querySelector("#loginIn");
let allButtons = document.querySelectorAll(".submit");
let loginInButton = document.querySelector("#switch-c1 .button");
let loginUpButton = document.querySelector("#switch-c2 .button");
let loginGifs = document.querySelectorAll(".loginGif");
let loginPics = document.querySelectorAll(".loginPic");

let getButtons = (e) => e.preventDefault()
let changeForm = (e) => {
    // 修改类名
    switchCtn.classList.add("is-gx");
    setTimeout(function() {
        switchCtn.classList.remove("is-gx");
    }, 1500)
    switchCtn.classList.toggle("is-txr");
    switchCircle[0].classList.toggle("is-txr");
    switchCircle[1].classList.toggle("is-txr");

    switchC1.classList.toggle("is-hidden");
    switchC2.classList.toggle("is-hidden");
    loginUp.classList.toggle("is-txl");
    loginIn.classList.toggle("is-txl");
    loginIn.classList.toggle("is-z");
}
let ani = (e) => {
        loginPics.forEach(loginPic => {
            loginPic.addEventListener("mouseover", () => {
                loginPic.classList.add("a");
                loginGifs.forEach(loginGif => loginGif.classList.remove("a"));
            });
        });

        loginGifs.forEach(loginGif => {
            loginGif.addEventListener("mouseout", () => {
                loginPics.forEach(loginPic => loginPic.classList.remove("a"));
                loginGif.classList.add("a");
            });
        });
    }
    // 点击切换
let login = (e) => {
        for (var i = 0; i < allButtons.length; i++)
            allButtons[i].addEventListener("click", getButtons);
        for (var i = 0; i < switchBtn.length; i++)
            switchBtn[i].addEventListener("click", changeForm);
    }
    //改变背景
let back = (e) => {
    loginInButton.addEventListener("click", function() {
        document.body.style.backgroundColor = "#c2dcea";
    });
    loginUpButton.addEventListener("click", function() {
        document.body.style.backgroundColor = "#365174";
    });
}

back();
ani();
window.addEventListener("load", login);

// 获取登录注册的盒子
const loadRegister = document.querySelectorAll('.form_button')
    // console.log(loadRegister)
    // 注册功能
loadRegister[0].addEventListener("click", function() {
        const un = document.querySelector('.logup_name')

        const pw = document.querySelector('.logup_pw')
        const username = un.value
        const password = pw.value
        let data = {
            username: username,
            password: password
        }
        console.log(data)
        axios({
                method: "POST",
                url: 'http://47.113.231.211:3000/users/register',
                data: {
                    username: username,
                    password: password
                }
            })
            .then(res => {
                console.log(res)
            })
            .catch(error => {
                console.log(error)
            })
    })
    //     ajax('47.113.231.211:3000/users/login', 'POST', data)
    //         .then(function(response) {
    //             // 处理成功响应
    //             console.log(response);
    //         })
    //         .catch(function(error) {
    //             // 处理请求失败或错误
    //             console.error(error);

//         });

// })
// 登录功能
loadRegister[1].addEventListener("click", function() {
        const un = document.querySelector('.login_name')
        const pw = document.querySelector('.login_pw')
        const username = un.value
        const password = pw.value
        let data = {
            username: username,
            password: password
        }
        console.log(data)
        axios({
                method: "POST",
                url: '47.113.231.211:3000/users/login',
                data: {
                    username: username,
                    password: password
                }
            })
            .then(res => {
                console.log(res)
            })
            .catch(error => {
                console.log(error)
            })
    })
    //     ajax('47.113.231.211:3000/users/login', 'POST', data)
    //         .then(function(response) {
    //             // 处理成功响应
    //             console.log(response);
    //         })
    //         .catch(function(error) {
    //             // 处理请求失败或错误
    //             console.error(error);

//         });

// })
// var xhr = new XMLHttpRequest();

// // 设置请求方法和 URL
// xhr.open("POST", "47.113.231.211:3000/users/login", true);

// // 设置请求头（如果有需要）
// xhr.setRequestHeader("Content-Type", "multipart/form-data");
// // 构造要发送的数据
// let data = {
//     username: '123',
//     password: '123'
// }
// console.log(data);
// // let jsonData = JSON.stringify(data)
// // console.log(jsonData);
// // let username = 'heyboy'
// // let password = 'aa12345'
// // console.log(username, password)
// // 将数据转换为 JSON 字符串
// // var jsonData = JSON.stringify(data);
// // console.log(jsonData)
// // 发送请求
// xhr.send(data);
// // 监听状jsonData态变化事件
// xhr.onreadystatechange = function() {
//     if (xhr.readyState === 4) {
//         if (xhr.status >= 200 || xhr.status < 300) {
//             // 请求成功，处理返回的数据
//             var response = xhr.responseText;
//             console.log('11');
//         } else {
//             // 请求失败，处理错误信息
//             console.error("请求失败：" + xhr.status);
//         }
//     }
// };



// 用promise封装ajax
// function ajax(url, method, data) {
//     return new Promise(function(resolve, reject) {
//         const xhr = new XMLHttpRequest();
//         xhr.open(method, url);
//         xhr.setRequestHeader('Content-Type', 'application/json');

//         xhr.onload = function() {
//             if (xhr.status >= 200 && xhr.status < 300) {
//                 resolve(xhr.responseText);
//             } else {
//                 reject(new Error('请求失败'));
//             }
//         };

//         xhr.onerror = function() {
//             reject(new Error('请求失败'));
//         };

//         xhr.send(JSON.stringify(data));
//     });
// }