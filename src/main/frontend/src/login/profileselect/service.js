async function makeRequest(address, request) {
    const response = await fetch(address, request)
    if (!response.ok) {
        const error = await response.json()
        console.log(error)
        throw new Error(error.customErrorCode)
    }
    return response
}

export async function getLinkedProfiles(sessionToken){
    const address = `${process.env.REACT_APP_DOMAIN}/linked-profiles`
    const headers = {
        'Authorization': sessionToken
    }
    const request = {
        "headers": headers,
        method: 'get'
    }
    const response = await makeRequest(address, request)
    return response
}