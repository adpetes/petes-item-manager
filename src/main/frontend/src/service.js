
async function makeRequest(address, request) {
    const requestWithCred = {
        ...request,
        credentials: 'include'
    };
    const response = await fetch(address, requestWithCred)
    if (!response.ok) {
        const error = await response.json();
        console.log(error)
        throw new Error(error.customErrorCode)
    }
    return response
}

export async function exchangeForToken(code){
    const headers = {
        "Authorization": code
    }
    const address = `${process.env.REACT_APP_DOMAIN}/oauth2redirect`
    const request = {
        method: 'get',
        "headers": headers
    }
    const response = await makeRequest(address, request)
    return response
}

export async function signIn(sessionToken){
    const headers = {
        "Authorization": sessionToken
    }
    const address = `${process.env.REACT_APP_DOMAIN}/signin`
    const request = {
        method: 'post',
        "headers": headers
    }
    const response = await makeRequest(address, request)
    console.log('sign in attempt successful: ', response.ok)
    return response
}

export async function signOut(sessionToken){
    const headers = {
        "Authorization": sessionToken
    }
    const address = `${process.env.REACT_APP_DOMAIN}/signout`
    const request = {
        method: 'post',
        "headers": headers
    }
    const response = await makeRequest(address, request)
    console.log('sign out attempt successful: ', response.ok)
    return response
}