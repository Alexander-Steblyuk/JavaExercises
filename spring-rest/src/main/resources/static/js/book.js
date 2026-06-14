$(function () {
    const authToken = localStorage.getItem('token')
    initAuthBtn(authToken)
    const bookId = window.location.pathname.split('/').at(-1)
    const path = '/api/v1/books/' + bookId
    $.get(path).done(book => fillForm (book, path))
})

function fillForm (book, url) {
    $('#title').append('Title: ' + book.title)
    $('#author').append('Author: ' + book.author.fullName)

    book.genres.forEach(function (genre) {
        $('#genres').append(`
            <li>${genre.name}</li>
        `)
    })

    const commentsUrl = url + '/comments'
    $.get(commentsUrl).done(function (comments) {
        let rowNum = 0
        comments.forEach(function (comment) {
            rowNum = rowNum + 1
            $('tbody').append(`
                <tr>
                    <td>${rowNum}</td>
                    <td>${comment.content}</td>
                </tr>
            `)
        })
    })
}