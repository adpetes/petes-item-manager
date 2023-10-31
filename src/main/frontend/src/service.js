import axios from 'axios';

async function makeRequest(address, request) {
    const updatedRequest = {
        ...request,
        credentials: 'include' // Include cookies with the request
    };
    const response = await fetch(address, updatedRequest)
    if (!response.ok) {
        const error = await response.json();
        console.log(error)
        throw new Error(error.customErrorCode)
    }
    return response
}

// export async function signIn(sessionToken){
//     const headers = {
//         "Authorization": sessionToken
//     }

//     try {
//         const response = await axios.post(`${process.env.REACT_APP_DOMAIN}/signin`, {}, {
//             headers: headers,
//             withCredentials: true
//         });

//         if (!response.data.success) {
//             throw new Error(response.data.error.customErrorCode);
//         }

//         return response.data;
//     } catch (error) {
//         console.error('Error:', error);
//         throw error;
//     }
// }

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