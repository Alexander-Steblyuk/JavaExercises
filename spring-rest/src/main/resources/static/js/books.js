$(function () {
    const authToken = localStorage.getItem('token')
    initAuthBtn(authToken)
    $.get('/api/v1/books').done(function (books) {
        const isAdmin = authToken != null ? parseJwt(authToken).role == 'ROLE_ADMIN' : false
        if (isAdmin) {
            $('table').before(`
                <p><a href="/books/add">Add a new book</a></p>
            `)
            $('tr').append(`
                <th>Edit</th>
                <th>Delete</th>
            `)
        }
        books.forEach(function (book) {
            const rowId = 'row' + book.id
            $('tbody').append(`
                <tr id="${rowId}">
                    <td><a href="/books/${book.id}">${book.title}</a></td>
                    <td>${book.author.fullName}</td>
                    <td>${getGenresString(book.genres)}</td>
                </tr>
            `)

            if (isAdmin) {
                const selector = '#' + rowId
                $(selector).append(`
                    <td><a href="/books/edit/${book.id}">Edit</a></td>
                    <td><a href="" onclick="doDelete(event, ${book.id})">Delete</a></td>
                `)
            }
        })
    })
})

function getGenresString (genres) {
    return genres.map(function(genre) {
        return genre.name
    })
}

function doDelete (event, bookId) {
    event.preventDefault()
    $.ajax({
        url: '/api/v1/books/' + bookId,
        type: 'DELETE',
        headers : getHeaders(),
        success: function (data, xhr) {
            if (xhr.status == 401) {
                localStorage.removeItem('token')
                throw new Error('UNAUTHORIZED')
            }
            window.location = 'books/edit/1'
        },
        error: function (xhr, thrown) {
            if (xhr.status == 401) {
                window.location = '/auth'
            }
            window.location.reload()
        }
    });
}