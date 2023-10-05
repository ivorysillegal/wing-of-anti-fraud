const btn = document.querySelector('.pw_btn')
console.log(btn)
const page = document.querySelector('.page')
console.log(page)
    // 一开始渲染一个页面
    // const answers = document.querySelector('.q_answer')
    // const expassword = document.querySelector('.exclusive_pw')
    // answers.innerHTML = ''
    // expassword.innerHTML = ''
    // axios({
    //         method: 'GET',
    //         url: 'http://47.113.231.211:3000/password',
    //     })
    //     .then(res => {
    //         // 接口数据
    //         console.log(res.data);
    //         let { data } = res.data
    //         problemRnder(res.data)
    //     })
    //     .catch(error => {
    //         // 处理错误
    //         console.error(error);
    //     });

const nextpage = document.querySelector('.nextpage')
    // 添加翻页事件，重新加载题目
nextpage.addEventListener('click', function() {
    page.style.display = 'block'
    page.style.zIndex = 2
    page.style.transform = 'rotateY(-170deg)'
    const answers = document.querySelector('.q_answer')
    const expassword = document.querySelector('.exclusive_pw')
    answers.innerHTML = ''
    expassword.innerHTML = ''
    let timer = setTimeout(function() {
        const page = document.querySelector('.page')
        page.style.display = 'none'
        page.style.zIndex = -1
        page.style.transform = 'rotateY(170deg)'
    }, 2100)
    axios({
            method: 'GET',
            // url: 'http://47.113.231.211:3000/password',
            url: 'http://localhost:3000/password',
        })
        .then(res => {
            // 接口数据
            console.log(res.data);
            let { data } = res.data
            problemRnder(data)
            const btn = document.querySelector('.pw_btn')
            console.log(btn);
            btn.addEventListener('click', function() {
                const q1 = document.getElementsByName('question1');
                console.log(q1);
                let an1;
                for (let i = 0; i < q1.length; i++) {
                    if (q1[i].checked) {
                        an1 = q1[i].value;
                        break;
                    }
                }
                const q2 = document.getElementsByName('question2');
                let an2;
                for (let i = 0; i < q2.length; i++) {
                    if (q2[i].checked) {
                        an2 = q2[i].value;
                        break;
                    }
                }
                const q3 = document.getElementsByName('question3');
                let an3;
                for (let i = 0; i < q3.length; i++) {
                    if (q3[i].checked) {
                        an3 = q3[i].value;
                        break;
                    }
                }
                const q4 = document.getElementsByName('question4');
                let an4;
                for (let i = 0; i < q4.length; i++) {
                    if (q4[i].checked) {
                        an4 = q4[i].value;
                        break;
                    }
                }
                const q5 = document.getElementsByName('question5');
                let an5;
                for (let i = 0; i < q5.length; i++) {
                    if (q5[i].checked) {
                        an5 = q5[i].value;
                        break;
                    }
                }
                console.log(an1, an2, an3, an4, an5)
                let Answer = [an1, an2, an3, an4, an5]
                // 将表单的值传给后端翻译
                axios({
                    method: "POST",
                    // url: 'http://47.113.231.211:3000/password',
                    url: 'http://localhost:3000/password',
                    data: {
                        Answer
                    }
                })
                    .then(res => {
                        console.log(res)
                        let { data } = res

                        console.log(data.data)
                        conservesion(data.data)
                    })
                    .catch(error => {
                        console.log(error)
                    })
            })

        })

})
        .catch(error => {
            // 处理错误
            console.error(error);
        });

//     console.log(btn);
//     btn.addEventListener('click', function() {
//         const q1 = document.getElementsByName('question1');
//         console.log(q1);
//         let an1;
//         for (let i = 0; i < q1.length; i++) {
//             if (q1[i].checked) {
//                 an1 = q1[i].value;
//                 break;
//             }
//         }
//         const q2 = document.getElementsByName('question2');
//         let an2;
//         for (let i = 0; i < q2.length; i++) {
//             if (q2[i].checked) {
//                 an2 = q2[i].value;
//                 break;
//             }
//         }
//         const q3 = document.getElementsByName('question3');
//         let an3;
//         for (let i = 0; i < q3.length; i++) {
//             if (q3[i].checked) {
//                 an3 = q3[i].value;
//                 break;
//             }
//         }
//         const q4 = document.getElementsByName('question4');
//         let an4;
//         for (let i = 0; i < q4.length; i++) {
//             if (q4[i].checked) {
//                 an4 = q4[i].value;
//                 break;
//             }
//         }
//         const q5 = document.getElementsByName('question5');
//         let an5;
//         for (let i = 0; i < q5.length; i++) {
//             if (q5[i].checked) {
//                 an5 = q5[i].value;
//                 break;
//             }
//         }
//         console.log(an1, an2, an3, an4, an5)
//         let Answer = [an1, an2, an3, an4, an5]
//         // 将表单的值传给后端翻译
//         axios({
//             method: "POST",
//             // url: 'http://47.113.231.211:3000/password',
//             url: 'http://localhost:3000/password',
//             data: {
//                 Answer
//             }
//         })
//             .then(res => {
//                 console.log(res)
//                 let { data } = res
//                 conservesion(data)
//             })
//             .catch(error => {
//                 console.log(error)
//             })
//     })
//
// })

function problemRnder(data) {
    const answers = document.querySelector('.q_answer')
    const expassword = document.querySelector('.exclusive_pw')
    answers.innerHTML = `<div class="pw_title">回答以下五道题后即可生成专属密码</div>
    <div class="pw_q">
        <div class="pw_q1">
            <div class="que1">1.${data[0].passwordQuestion}</div>
            <div class="answer1"><label for="q1_c1"><input id="q1_c1" type="radio" name="question1" value="${data[0].choice1}">${data[0].choice1}</label><label for="q1_c2"><input type="radio" id="q1_c2"name="question1" value="${data[0].choice2}">${data[0].choice2}</label><label for="q1_c3"><input type="radio" id="q1_c3"name="question1" value="${data[0].choice3}">${data[0].choice3}</label>
                <label for="q1_c4"><input type="radio" id="q1_c4"name="question1" value="${data[0].choice4}">${data[0].choice4}</label>
            </div>
        </div>
        <div class="pw_q2">
            <div class="que2">2.${data[1].passwordQuestion}</div>
            <div class="answer2"><label for="q2_c1"><input type="radio" id="q2_c1" 
            checked name="question2" value="${data[1].choice1}">${data[1].choice1}</label><label for="q2_c2"><input type="radio" id="q2_c2"  name="question2" value="${data[1].choice2}">${data[1].choice2}</label><label for="q2_c3"><input type="radio" id="q2_c3" name="question2" value="${data[1].choice3}">${data[1].choice3}</label>
                <label for="q2_c4"><input type="radio" id="q2_c4" name="question2" value="${data[1].choice4}">${data[1].choice4}</label>
            </div>
        </div>
        <div class="pw_q3">
            <div class="que3">3.${data[2].passwordQuestion}</div>
            <div class="answer3"><label for="q3_c1"><input type="radio" id="q3_c1" checked name="question3" value="${data[2].choice1}">${data[2].choice1}</label><label for="q3_c2"><input type="radio" id="q3_c2"  name="question3" value="${data[2].choice2}">${data[2].choice2}</label><label for="q3_c3"><input type="radio" id="q3_c3"  name="question3" value="${data[2].choice3}">${data[2].choice3}</label>
                <label for="q3_c4"><input type="radio" id="q3_c4"  name="question3" value="${data[2].choice4}">${data[2].choice4}</label>
            </div>
        </div>
        <div class="pw_q4">
            <div class="que4">4.${data[3].passwordQuestion}</div>
            <div class="answer4"><label  for="q4_c1"><input type="radio" id="q4_c1" checked  name="question4" value="${data[3].choice1}">${data[3].choice1}</label><label for="q4_c2"><input type="radio"  id="q4_c2" name="question4" value="${data[3].choice2}">${data[3].choice2}</label><label for="q4_c3"><input type="radio"  id="q4_c3" name="question4" value="${data[3].choice3}">${data[3].choice3}</label>
                <label for="q4_c4"><input type="radio"  id="q4_c4" name="question4" value="${data[3].choice4}">${data[3].choice4}</label>
            </div>
        </div>
        <div class="pw_q5">
            <div class="que5">5.${data[4].passwordQuestion}</div>
            <div class="answer5"><label for="q5_c1"><input type="radio" id="q5_c1" checked name="question5" value="${data[4].choice1}">${data[4].choice1}</label><label for="q5_c2"><input type="radio" id="q5_c2"  name="question5" value="${data[4].choice2}">${data[4].choice2}</label><label for="q5_c3"><input type="radio" id="q5_c3"  name="question5" value="${data[4].choice3}">${data[4].choice3}</label>
                <label for="q5_c4"><input type="radio" id="q5_c4"  name="question5" value="${data[4].choice4}">${data[4].choice4}</label>
            </div>
        </div>
        <button class="pw_btn">开始生成
        </button>
    </div>`
    expassword.innerHTML = `  <div class="product_pw">为您生成的专属密码为：</div>
    <div class="pw_result"></div>
    <div class="unsatisfy">
        <div class="pw_again">不满意？重新再次生成...</div><span></span>
       
    </div>
    <div class="nextpage"></div>`
}

// 给开始生成盒子添加点击事件获取获取单选框的值并随机组成一段密码
// const product = document.querySelector('.pw_btn')
// btn.addEventListener('click', function() {
//     let pwArr = ['!', '?', '.', '@', '#', '$', '%', '&', '*', ',']
// btn.addEventListener('click', function() {
//     const q1 = document.getElementsByName('question1');
//     console.log(q1);
//     let an1;
//     for (let i = 0; i < q1.length; i++) {
//         if (q1[i].checked) {
//             an1 = q1[i].value;
//             break;
//         }
//     }
//     const q2 = document.getElementsByName('question2');
//     let an2;
//     for (let i = 0; i < q2.length; i++) {
//         if (q2[i].checked) {
//             an2 = q2[i].value;
//             break;
//         }
//     }
//     const q3 = document.getElementsByName('question3');
//     let an3;
//     for (let i = 0; i < q3.length; i++) {
//         if (q3[i].checked) {
//             an3 = q3[i].value;
//             break;
//         }
//     }
//     const q4 = document.getElementsByName('question4');
//     let an4;
//     for (let i = 0; i < q4.length; i++) {
//         if (q4[i].checked) {
//             an4 = q4[i].value;
//             break;
//         }
//     }
//     const q5 = document.getElementsByName('question5');
//     let an5;
//     for (let i = 0; i < q5.length; i++) {
//         if (q5[i].checked) {
//             an5 = q5[i].value;
//             break;
//         }
//     }
//     console.log(an1, an2, an3, an4, an5)
//     let Answer = [an1, an2, an3, an4, an5]
//         // 将表单的值传给后端翻译
//     axios({
//             method: "POST",
//             // url: 'http://47.113.231.211:3000/password',
//             url: 'http://localhost:3000/password',
//             data: {
//                 Answer
//             }
//         })
//         .then(res => {
//             console.log(res)
//             let { data } = res
//             conservesion(data)
//         })
//         .catch(error => {
//             console.log(error)
//         })
// })

// 字符串拼接组成密码
function conservesion(data) {
    let pwArr = ['!', '?', '.', '@', '#', '$', '%', '&', '*', ',']
    const index1 = Math.floor(Math.random() * pwArr.length);
    const index2 = Math.floor(Math.random() * pwArr.length);
    let graph = [pwArr[index1], pwArr[index2]]
    data = data.split(',')
    const pwarr = [...graph, ...data]
    shuffleArray(pwarr)
    let pwarrstr = pwarr.join(',')
    const product_pw = document.querySelector('.product_pw')
    product_pw.innerHTML = pwarrstr
}

function shuffleArray(arr) {
    let n = arr.length;
    for (let i = n - 1; i > 0; i--) {
        let j = Math.floor(Math.random() * (i + 1));
        let temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}