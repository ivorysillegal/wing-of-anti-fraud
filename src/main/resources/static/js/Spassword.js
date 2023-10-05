const btn = document.querySelector('button')
console.log(btn)
const page = document.querySelector('.page')
console.log(page)
btn.addEventListener('click', function() {
        page.style.transform = 'rotateY(-170deg)'
        const answers = document.querySelector('.q_answer')
        const expassword = document.querySelector('.exclusive_pw')
        answers.innerHTML = ''
        expassword.innerHTML = ''
        axios({
                method: 'GET',
                // url: 'http://47.113.231.211:3000/password',
                url: 'http://localhost:3000/password',

            })
            .then(res => {
                // 接口数据

                console.log(res.data);
                let{data}=res.data
                problemRnder(data)
            })
            .catch(error => {
                // 处理错误
                console.error(error);
            });

    })
    // axios({
    //         method: 'GET',
    //         url: 'http://47.113.231.211:3000/password',
    //     })
    //     .then(res => {
    //         // 接口数据
    //         console.log(res.data);
    //         problemRnder(res.data)
    //     })
    //     .catch(error => {
    //         // 处理错误
    //         console.error(error);
    //     });

function problemRnder(data) {
    const answers = document.querySelector('.q_answer')
    const expassword = document.querySelector('.exclusive_pw')
    answers.innerHTML = `<div class="pw_title">回答以下五道题后即可生成专属密码</div>
    <div class="pw_q">
        <div class="pw_q1">
            <div class="que1">1.${data[0].passwordQuestion}</div>
            <div class="answer1"><label for=""><input type="radio" name="question1" value="${data[0].choice1}">${data[0].choice1}</label><label for=""><input type="radio" name="question1" value="${data[0].choice2}">${data[0].choice2}</label><label for=""><input type="radio" name="question1" value="${data[0].choice3}">${data[0].choice3}</label>
                <label for=""><input type="radio" name="question1" value="${data[0].choice4}">${data[0].choice4}</label>
            </div>
        </div>
        <div class="pw_q2">
            <div class="que2">2.${data[1].passwordQuestion}</div>
            <div class="answer2"><label for=""><input type="radio" name="question2" value="${data[1].choice1}">${data[1].choice1}</label><label for=""><input type="radio" name="question2" value="${data[1].choice2}">${data[1].choice2}</label><label for=""><input type="radio" name="question2" value="${data[1].choice3}">${data[1].choice3}</label>
                <label for=""><input type="radio" name="question2" value="${data[1].choice4}">${data[1].choice4}</label>
            </div>
        </div>
        <div class="pw_q3">
            <div class="que3">3.${data[2].passwordQuestion}</div>
            <div class="answer3"><label for=""><input type="radio" name="question3" value="${data[2].choice1}">${data[2].choice1}</label><label for=""><input type="radio" name="question3" value="${data[2].choice2}">${data[2].choice2}</label><label for=""><input type="radio" name="question3" value="${data[2].choice3}">${data[2].choice3}</label>
                <label for=""><input type="radio" name="question3" value="${data[2].choice4}">${data[2].choice4}</label>
            </div>
        </div>
        <div class="pw_q4">
            <div class="que4">4.${data[3].passwordQuestion}</div>
            <div class="answer4"><label for=""><input type="radio" name="question4" value="${data[3].choice1}">${data[3].choice1}</label><label for=""><input type="radio" name="question4" value="${data[3].choice2}">${data[3].choice2}</label><label for=""><input type="radio" name="question4" value="${data[3].choice3}">${data[3].choice3}</label>
                <label for=""><input type="radio" name="question4" value="${data[3].choice4}">${data[3].choice4}</label>
            </div>
        </div>
        <div class="pw_q5">
            <div class="que5">5.${data[4].passwordQuestion}</div>
            <div class="answer5"><label for=""><input type="radio" name="question5" value="${data[4].choice1}">${data[4].choice1}</label><label for=""><input type="radio" name="question5" value="${data[4].choice2}">${data[4].choice2}</label><label for=""><input type="radio" name="question5" value="${data[4].choice3}">${data[4].choice3}</label>
                <label for=""><input type="radio" name="question5" value="${data[4].choice4}">${data[4].choice4}</label>
            </div>
        </div>
    </div>`

}