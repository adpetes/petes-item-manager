import React, { useMemo } from 'react'
import { useEffect } from 'react'
import { useParams } from 'react-router-dom'
import CharacterInfoDisplay from './characterdisplay/CharacterInfoDisplay'
import Cookies from 'js-cookie'
import { getDetailedProfile } from './service'
import { useState } from 'react'
import Navbar from '../navbar/Navbar'
import './pim.css'
import NotificationWindow from './notification/NotificationWindow'
import Inventory from './Inventory'
import GridLoader from "react-spinners/GridLoader";

function Pim( props ) {
  const { signOut, setErrorMessage, isDemo } = props
  const params = useParams()
  const urlParams = new URLSearchParams(window.location.search);

  const [characters, setCharacters] = useState(null)
  const [transferNotiInfo, setTransferNotiInfo] = useState(null)
  const [searchText, setSearchText] = useState(null)
  const [isFetchingProfile, setIsFetchingProfile] = useState(false);

  const memoizedSetTransferNotiInfo = useMemo(() => setTransferNotiInfo, []); // Memoize setTransferNotiInfo

  const getProfileData = async () => {
    setIsFetchingProfile(true)
    try {
      const sessionToken = !isDemo ? Cookies.get("sessionToken") : process.env.REACT_APP_DEMO_TOKEN;
      const accountId = !isDemo ? Cookies.get("accountId") : process.env.REACT_APP_DEMO_ACCOUNT_ID;
      const {profileId, membershipType} = params

      if (sessionToken && accountId) {
        const profileResponse = await getDetailedProfile(sessionToken, accountId, profileId, membershipType)
        const profileData = await profileResponse.json()
        
        const characterList = []
        for (const characterKey in profileData) {
          characterList.push(profileData[characterKey])
        }
        setCharacters(characterList)
        console.log("profile data: ", characterList)
      }
      else {
        await signOut()
      }
    } 
    catch (error) {
      setErrorMessage(error.message)
    }
    setIsFetchingProfile(false)
  }

  useEffect(() => {
    if (String(isDemo) !== urlParams.get('isDemo')) {
      handleSignOut()
    }
    if (isDemo) {
      setTransferNotiInfo({isDemoNoti: true})
    }
  }, [])

  useEffect(() => {
    if (!characters || (transferNotiInfo && transferNotiInfo.result.errorCode)) {
      const timer = setTimeout(() => {
        getProfileData();
      }, (transferNotiInfo && transferNotiInfo.result) ? 5000 : 0);
  
      // Clean up the timer if the component is unmounted or the effect is re-run
      return () => clearTimeout(timer);
    }
  }, [transferNotiInfo])

  const handleRefresh = async () =>  {
    await getProfileData();
  }

  const handleSignOut = async () => {
    await signOut()
  }

  const getCharacterInfo = () => {
    let info = []
    for (const characterIdx in characters) {
        const character = characters[characterIdx]
        if (character.hasOwnProperty("characterClass")) {
          info.push({
            name: character.characterClass,
            light: character.light,
            emblem: character.emblem
          })
        }
        else {
          info.push({
            name: "Vault",
            emblem: {
              iconUrl: "/common/destiny2_content/icons/e7f00ab9bf2a5275dcd2d9b1e9efe84c.jpg"
            }
          })
        }
    }
    return info
  }

  return (
    <>
      {isFetchingProfile && <div className='page-loading'>
        <GridLoader
          color={'white'}
          size={20}
          loading={isFetchingProfile}
        />
        <p className='loading-text'> Retrieving Profile Data... </p>
      </div>}
      <div className='pim-header'>
        <Navbar showSearch={true} setSearchText={setSearchText} handleRefresh={handleRefresh} isRefreshing={isFetchingProfile} handleSignOut={handleSignOut}/>
        <CharacterInfoDisplay characters={getCharacterInfo()}/>
      </div>
      <NotificationWindow transferNotiInfo={transferNotiInfo} setTransferNotiInfo={memoizedSetTransferNotiInfo} />
      {characters && <Inventory signOut={signOut} setTransferNotiInfo={memoizedSetTransferNotiInfo} setErrorMessage={setErrorMessage} characters={characters} searchText={searchText} isDemo={isDemo} />}
    </>
  )
}

export default Pim