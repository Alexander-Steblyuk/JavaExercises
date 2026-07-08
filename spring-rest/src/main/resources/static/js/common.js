function initAuthBtn(token) {
    const authBtn = document.getElementById('auth')
    if (token != null) {
        authBtn.textContent = 'Sign Out'
    } else {
        authBtn.textContent = 'Sign In'
    }
}

function doSignInOrOut(event) {
    event.preventDefault()
    const authToken = localStorage.getItem('token')
    const authBtn = document.getElementById('auth')
    if (authToken != null) {
        localStorage.removeItem('token')
        window.location.reload()
    } else {
        window.location = '/auth'
    }
}

function getHeaders() {
    let headers
    const authToken = localStorage.getItem('token')
    if (authToken != null) {
        const authHeader = 'Bearer ' + authToken
        headers = { 'Content-Type': 'application/json', 'Authorization' : authHeader }
    } else {
        headers = { 'Content-Type': 'application/json' }
    }
    return headers
}

function parseJwt(token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}