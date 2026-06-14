$(function () {
    const authToken = localStorage.getItem('token')
    checkAuthToken(authToken)
    initAuthBtn(authToken)
    const bookId = window.location.pathname.split('/').at(-1)
    if (isNaN(bookId)) {
        const emptyBook = getEmptyBook();
        fillForm(emptyBook)
    } else {
        const path = '/api/v1/books/' + bookId
        $.get(path).done(book => fillForm (book))
    }
})

function fillForm (book) {
    document.getElementById('id').value = book.id
    document.getElementById('title').value = book.title

    let authors
    $.get('/api/v1/authors').done(function (result) {
        authors = result
        authors.forEach(function (author) {
            let option = `<option value="${author.id}">${author.fullName}</option>`
            if (book.author != null && book.author.id == author.id) {
                option = `<option value="${author.id}" selected=true>${author.fullName}</option>`
            }
            $('#author').append(option)
        })
    })

    let genres
    $.get('/api/v1/genres').done(function (result) {
        genres = result
        let bookGenresIds = getGenresIds(book.genres)
        genres.forEach(function (genre) {
            let option = `<option value="${genre.id}">${genre.name}</option>`
            if (bookGenresIds.includes(genre.id)) {
                option = `<option value="${genre.id}" selected=true>${genre.name}</option>`
            }
            $('#genres').append(option)
        })
    })

    const form = document.getElementById('bookForm')
    form.addEventListener('submit', function(event) {
        event.preventDefault()
        const idInput = document.getElementById('id')
        const titleInput = document.getElementById('title')
        const authorInput = document.getElementById('author')
        const genresInput = document.getElementById('genres')

        const authorValue = getAuthorById(authors, authorInput.value)
        const genresValue = getGenresByIds(genres, genresInput.selectedOptions)

        const book = { id: idInput.value, title: titleInput.value, author: authorValue, genres: genresValue }
        const bookJsonString = JSON.stringify(book);
        const requestMethod = book.id == null ? 'POST' : 'PUT'
        const requestHeaders = getHeaders()
        fetch('/api/v1/books', { method: requestMethod, headers: requestHeaders, body: bookJsonString })
        .then(response => {
            if (!response.ok) {
                const error = new Error('Ошибка при сохранении книги ' + idInput.value)
                error.status = response.status
                throw error
            }
            window.location = "/books"
        })
        .catch(error => {
            console.error('Ошибка:', error)
            if (error.status == 401 || error.status == 403) {
                localStorage.removeItem('token')
                window.location = "/auth"
            }
            window.location.reload()
        })
    });
}

function getEmptyBook() {
    return { id: null, title: '', author: null, genres: [] }
}

function getGenresIds (genres) {
    return genres.map(function(genre) {
        return genre.id
    })
}

function getAuthorById (authors, selectedId) {
    return authors.find(author => author.id == selectedId)
}

function getGenresByIds (genres, selectedOptions) {
    const selectedIds = Array.from(selectedOptions).map(option => parseInt(option.value))
    return genres.filter(genre => selectedIds.includes(genre.id))
}

function checkAuthToken(token) {
    if (token == null) {
        window.location = "/auth"
    }
    const isNotAdmin = parseJwt(token).role != 'ROLE_ADMIN'
    if (isNotAdmin) {
        window.location = "/error"
    }
}