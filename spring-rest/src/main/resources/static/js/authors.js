 $(function () {
    const authToken = localStorage.getItem('token')
    initAuthBtn(authToken)
    $.get('/api/v1/authors').done(function (authors) {
        let rowNum = 0;
        authors.forEach(function (author) {
            rowNum = rowNum + 1
            $('tbody').append(`
                <tr>
                    <td>${rowNum}</td>
                    <td>${author.fullName}</td>
                </tr>
            `)
        })
    })
})