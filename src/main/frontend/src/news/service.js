async function makeRequest(address, request) {
    const response = await fetch(address, request)
    if (!response.ok) {
        const error = await response.json();
        console.log("hi", error)
        throw new Error(error.customErrorCode)
    }
    return response
}

export async function getMilestoneRotation() {
    const address = `${process.env.REACT_APP_DOMAIN}/milestone-rotation`
    const request = {
        method: 'get'
    }
    const response = await makeRequest(address, request)
    return response
}