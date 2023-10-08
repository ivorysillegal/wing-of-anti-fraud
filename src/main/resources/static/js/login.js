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
                loginPics.forEach(loginGif => loginGif.classList.remove("a"));
                loginPic.classList.add("a");
                loginGifs.forEach(loginGif => loginGif.classList.remove("a"));
            });
        });


        loginGifs.forEach(loginGif => {
            loginGif.addEventListener("mouseout", () => {
                loginGifs.forEach(item => item.classList.remove("a"));
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
    // 改变背景
let back = (e) => {
    loginInButton.addEventListener("click", function() {
        document.body.style.backgroundImage = "url(../images/banner1.png)";
    });
    loginUpButton.addEventListener("click", function() {
        document.body.style.backgroundImage = "url(../images/banner0.png)";
    });
}

back();
ani();
window.addEventListener("load", login);
// 获取登录注册的盒子
const loadRegister = document.querySelectorAll('.form_button')
console.log(loadRegister)
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
            console.log(res.data)
            let {
                code
            } = res.data
            if (code == 202) {
                alert('用户名重复,请重新填写')
            } else if (code == 201 || code == 200) {
                alert('注册成功')
            }

        })
        .catch(error => {
            console.log(error)
        })
})

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
            url: 'http://47.113.231.211:3000/users/login',
            data: {
                username: username,
                password: password
            }
        })
        .then(res => {
            console.log(res.data)

            let {
                code
            } = res.data
            if (code == 201) {
                alert('用户名或密码错误')
            } else if (code == 200) {

                window.location.href = "index.html";
            }

        })
        .catch(error => {
            console.log(error)
        })
})