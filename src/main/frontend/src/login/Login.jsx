import React, { useEffect, useState } from 'react'
import { getAuthorizationUrl } from './service';
import Navbar from '../navbar/Navbar'
import './login.css'
import { Link } from 'react-router-dom';
import Cookies from 'js-cookie';

function Login(props) {
  const { setErrorMessage, setIsDemo } = props

  const [authUrl, setAuthUrl] = useState(null)
  const [reauthUrl, setReauthUrl] = useState(null)

  
  const handleAuthorizeButtonClick = (reauth) => {
    if (authUrl) {
      if (!reauth) {
        window.location.href = authUrl;
      }
      else {
        window.location.href = reauthUrl;
      }
    }
  }
  
  useEffect(() => {
    const getAuthUrl = async () => {
      try {
        const authUrlResponse = await getAuthorizationUrl()
        const urls = await authUrlResponse.json()
        setAuthUrl(urls.authUrl)
        setReauthUrl(urls.reauthUrl)
      } 
      catch (error) {
        console.log(error)
        setErrorMessage(error.message)
      }
    }
    getAuthUrl()
  }, []);

  return (
    <>
      <Navbar showSearch={false}/>
      <div className='login-container'>
        <p className='text-header'> WE NEED YOUR PERMISSION...</p>
        <p className='text-notice'> Allow PIM to view and modify your Destiny characters, vault, and progression. </p>
        {authUrl && <button className='authorize-button' onClick={() => handleAuthorizeButtonClick(false)}>
          Authorize with Bungie.net
        </button>}
        {reauthUrl && <button className='reauthorize-button' onClick={() => handleAuthorizeButtonClick(true)}>
          Log in with a different Bungie.net account
        </button>}
        <Link className='demo-mode-link' onClick={() => setIsDemo(true)} to={`/`}>
          Don't have a Destiny 2 account? Try Demo Mode
        </Link>

      </div>
    </>
  )
}

export default Login