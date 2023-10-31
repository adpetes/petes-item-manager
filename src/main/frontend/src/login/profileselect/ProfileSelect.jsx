import React, { useEffect, useState } from 'react'
import Navbar from '../../navbar/Navbar';
import GridLoader from "react-spinners/GridLoader";
import Cookies from 'js-cookie';
import { getLinkedProfiles } from './service';
import './profileselect.css'
import xbox from '../../images/xbox.png'
import psn from '../../images/psn.png'
import steam from '../../images/steam.png'
import { FaSignOutAlt } from 'react-icons/fa';
import { Link } from 'react-router-dom';


function ProfileSelect( props ) {
    const { signOut, setErrorMessage } = props
    const [accountInfo, setAccountInfo] = useState(null)

  useEffect(() => {
    const getAccountInfo = async () => {
      try {
        const sessionToken = Cookies.get('sessionToken')
        const linkedProfilesResponse = await getLinkedProfiles(sessionToken)
        const linkedProfiles = await linkedProfilesResponse.json()
        console.log("linked profiles: ", linkedProfiles)
        const info = {accountName: linkedProfiles.Response.bnetMembership.supplementalDisplayName, accountId: linkedProfiles.Response.bnetMembership.membershipId, profiles: linkedProfiles.Response.profiles}
        setAccountInfo(info)
      } 
      catch (error) {
        setErrorMessage(error.message)
      }
    }
    getAccountInfo()
  }, [])

  const getAccountName = () => accountInfo.accountName

  const getPlatform = (membershipType) => {
    switch (membershipType) {
      case 1: 
        return xbox
      case 2: 
        return psn
      case 3: 
        return steam
      case 4: 
        return "Blizzard"
      case 5: 
        return "Stadia"
      case 6: 
        return "EGS"
      case 10: 
        return "Demon"
      default:
        return ""
    }
  }

  const handleSignOutClick = async () => {
    await signOut()
  }

  const handleProfileSelectClick = (profile) => {
    Cookies.set("accountId", accountInfo.accountId)
    Cookies.set("profileId", profile.membershipId)
    Cookies.set("membershipType", profile.membershipType)
  }

  const getPath = (profile) => {
    const queryParam = new URLSearchParams()
    queryParam.append('isDemo', String(false))
    return `/inventory/${profile.membershipId}/${profile.membershipType}/?${queryParam.toString()}`
  }
  
  return (
    <>
      <Navbar showSearch={false}/>
      {accountInfo ? 
        <div className="profile-select-container">
          <p className='p-account-name'>Profiles for {getAccountName()}</p>
          {accountInfo.profiles.map(profile => (
            <Link key={profile.membershipId} className='profile-box' onClick={() => handleProfileSelectClick(profile)} to={getPath(profile)}>
              <img src={getPlatform(profile.membershipType)} alt={profile.membershipType} className='platform-img'/>
              <span className='profile-name'>{profile.displayName}</span>
            </Link>
          ))}
          <p className='p-missing'>If you don't see your account here, you may not have logged in to the right Bungie.net account, or Bungie.net may be down for maintenance. You can switch accounts later from the menu in the header.</p>
          <div className='profile-select-logout' onClick={handleSignOutClick}>
            <FaSignOutAlt/> 
            <span> Logout</span>
          </div>
        </div>
      : 
      <div className='page-loading'>
        <GridLoader
          color={'white'}
          size={20}
          loading={true}
        />
        <p className='loading-text'> Retrieving Linked Profiles... </p>
      </div>}
    </>
  );
}

export default ProfileSelect