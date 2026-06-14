$(function () {
    const authToken = localStorage.getItem('token')
    initAuthBtn(authToken)
    $.get('/api/v1/genres').done(function (genres) {
        let rowNum = 0;
        genres.forEach(function (genre) {
            rowNum = rowNum + 1
            $('tbody').append(`
                <tr>
                    <td>${rowNum}</td>
                    <td>${genre.name}</td>
                </tr>
            `)
        })
    })
})