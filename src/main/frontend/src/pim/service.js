async function makeRequest(address, request) {
    const updatedRequest = {
        ...request,
        credentials: 'include' // Include cookies with the request
    };
    const response = await fetch(address, updatedRequest)
    if (!response.ok) {
        const error = await response.json()
        console.log(error)
        throw new Error(error.customErrorCode)
    }
    return response
}

// async function makeTransferRequest(address, request) {
//     const response = await fetch(address, request)
//     if (!response.ok) {
//         const error = await response.json()
//         console.log("Error: ", error)
//         throw new Error(error.customErrorCode) 
//     }
//     return response
// }

export async function getProfile(sessionToken, profileId, membershipType){
    const headers = {
        "Authorization": sessionToken
    }
    const address = `${process.env.REACT_APP_DOMAIN}/profile/${profileId}/${membershipType}`
    const request = {
        method: 'get',
        "headers": headers
    }
    const response = await makeRequest(address, request)
    // console.log('got profile data: ', response)
    return response
}

export async function getDetailedProfile(sessionToken, accountId, profileId, membershipType){
    const headers = {
        "Authorization": sessionToken,
        "Account-Id": accountId,
    }
    const address = `${process.env.REACT_APP_DOMAIN}/profile-detailed/${profileId}/${membershipType}`
    const request = {
        method: 'get',
        "headers": headers
    }
    const response = await makeRequest(address, request)
    // console.log('got profile data: ', response)
    return response
}

export async function transferItem(sessionToken, accountId, profileId, membershipType, itemData, sourceCharacterId, targetCharacterId, toVault, isDemo) {
    const headers = {
        "Authorization": sessionToken,
        "Account-Id": accountId,
        "Content-Type": "application/json",
        "Demo": isDemo
    }
    const body = {
        inventoryItemInstance: {
            instanceItemId: itemData.inventoryItemInstance.instanceItemId
        },
        inventoryItem: {
            hashVal: itemData.hashVal
        },
        stackSize: 1,
        sourceCharacterId: sourceCharacterId && sourceCharacterId,
        targetCharacterId: targetCharacterId,
        toVault: toVault
    }
    const address = `${process.env.REACT_APP_DOMAIN}/transfer-item/${profileId}/${membershipType}`
    const request = {
        method: 'post',
        headers: headers,
        body: JSON.stringify(body)
    }
    const response = await makeRequest(address, request)
    return response
}

export async function equipItem(sessionToken, accountId, profileId, membershipType, itemData, sourceCharacterId, targetCharacterId, isDemo) {
    const headers = {
        "Authorization": sessionToken,
        "Account-Id": accountId,
        "Content-Type": "application/json",
        "Demo": isDemo
    }
    const body = {
        inventoryItemInstance: {
            instanceItemId: itemData.inventoryItemInstance.instanceItemId
        },
        inventoryItem: {
            hashVal: itemData.hashVal
        },
        sourceCharacterId: sourceCharacterId,
        targetCharacterId: targetCharacterId,
    }
    const address = `${process.env.REACT_APP_DOMAIN}/equip-item/${profileId}/${membershipType}`
    const request = {
        method: 'post',
        headers: headers,
        body: JSON.stringify(body)
    }
    const response = await makeRequest(address, request)
    return response
}