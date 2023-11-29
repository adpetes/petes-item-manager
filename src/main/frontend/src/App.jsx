import { React, useEffect, useState } from 'react';
import './App.css';
import ProfileSelect from './login/profileselect/ProfileSelect';
import Cookies from 'js-cookie';
import { exchangeForToken, signIn, signOut } from './service';
import GridLoader from "react-spinners/GridLoader";
import Error from './error/Error';
import Login from './login/Login';
import { Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import Pim from './pim/Pim';
import News from './news/News';
import About from './about/About';

function App() {

  const [errorMessage, setErrorMessage] = useState(null);
  const [isCheckingAuthorization, setIsCheckingAuthorization] = useState(true);
  const [isAuthorized, setIsAuthorized] = useState(false);
  const [isDemo, setIsDemo] = useState(false);
  // const [token, setToken] = useState(null);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const checkAuthorization = async (token) => {
      try {
        if (token) {
          Cookies.set("sessionToken", token, { expires: 7 })
          await signIn(token)
          setIsAuthorized(true)
        }
        else {
          await handleSignOut()
        }
      } 
      catch (error) {
        if (error.message !== '2') {
          setErrorMessage(error.message)
        }
      }
      setIsCheckingAuthorization(false)
    }

    const queryParams = new URLSearchParams(location.search);
    let token = queryParams.get('sessionId');
    if (!token) {
      token = Cookies.get("sessionToken")
    }
    checkAuthorization(token)
  }, []);

  useEffect(() => {
    if (errorMessage) {
      navigate('/error')
    }
  }, [errorMessage])

  const handleSignOut = async () => {
    if (isDemo) {
      setIsDemo(false)
    }
    try {
      const sessionToken = Cookies.get("sessionToken")

      if (sessionToken) {
        await signOut(sessionToken)
      }
      const cookies = Cookies.get();
      Object.keys(cookies).forEach(cookieName => {
        Cookies.remove(cookieName);
      });
      setIsAuthorized(false)
    } 
    catch (error) {
      console.log("Error signing out: ", error)
      setErrorMessage(error.message)
    }
  }

  const determinePath = () => {
    const queryParam = new URLSearchParams()
    queryParam.append('isDemo', String(isDemo))

    if (isAuthorized) {
      const profileId = Cookies.get("profileId")
      const membershipType = Cookies.get("membershipType")
      if (profileId && membershipType) {
        return <Navigate to={`/inventory/${profileId}/${membershipType}/?${queryParam.toString()}`} />
      }
      else {
        return <Navigate to='/inventory/profile-select' />
      }
    }
    else if (isDemo) {
      return <Navigate to={`/inventory/${process.env.REACT_APP_DEMO_PROFILE_ID}/${process.env.REACT_APP_DEMO_MEMBERSHIP_TYPE}?${queryParam.toString()}`} />
    }
    else {
      return <Navigate to='/inventory/login' />
    }
  }
  
  return (
      !isCheckingAuthorization ? 
      <>
        <Routes>
          <Route path="/inventory/login" element={<Login setErrorMessage={setErrorMessage} setIsDemo={setIsDemo} />} />
          <Route path="/inventory/profile-select" element={isAuthorized ? <ProfileSelect signOut={handleSignOut} setErrorMessage={setErrorMessage} /> : <Navigate to="/inventory/login" />} />
          <Route path="/inventory/:profileId/:membershipType" element={(isAuthorized || isDemo) ? <Pim signOut={handleSignOut} setErrorMessage={setErrorMessage} isDemo={isDemo} /> : <Navigate to="/inventory/login" />} />
          <Route path="/inventory" element={determinePath()}/>
          <Route path="/about" element={<About handleSignOut={handleSignOut}/>}/>
          <Route path="/" element={<News setErrorMessage={setErrorMessage} handleSignOut={handleSignOut} />}/>
          <Route path="/*" element={<Error error={'404'}/>} />
          <Route path="/error" element={<Error error={errorMessage} />} />
        </Routes>
      </>
      : 
      <div className='page-loading'>
          <GridLoader
            color={'white'}
            size={20}
            loading={true}
          />
          <p className='loading-text'> Checking Authorization... </p>
      </div>
  )
}

export default App;
