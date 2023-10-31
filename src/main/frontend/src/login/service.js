async function makeRequest(address, request) {
    const response = await fetch(address, request)
    if (!response.ok) {
        const error = await response.json()
        console.log(error)
        throw new Error(error.customErrorCode)
    }
    return response
}

export async function getAuthorizationUrl(){
    const address = `${process.env.REACT_APP_DOMAIN}/bungie-authorize-url`
    const request = {
        method: 'get'
    }
    const response = await makeRequest(address, request)
    return response
}