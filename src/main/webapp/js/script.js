
const formElement = document.getElementById('startedForm'); // извлекаем элемент формы

const sendFormRequest = (e) => {
    e.preventDefault();

    const formData = new FormData(formElement);
    let playerOneName = formData.get("playerOneName");
    let playerTwoName = formData.get("playerTwoName");

    fetch('new-match', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            playerOneName,
            playerTwoName
        }) // Ваши данные для отправки
    })

        .then((response) => {
            if (response.ok) {
                makeRedirect();
            }
        })

        .catch(error => console.error('Ошибка:', error));
}

const makeRedirect = () => {
    document.location.href = '../match-score.html';
}

formElement.addEventListener('submit', sendFormRequest);
// formElement.addEventListener('submit', makeRedirect);