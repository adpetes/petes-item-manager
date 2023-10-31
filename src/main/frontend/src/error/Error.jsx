import React from 'react'
import './error.css'
import { useNavigate } from 'react-router-dom';


function Error( {error} ) {
  const navigate = useNavigate();

  const getErrorText = () => {
      // console.log(error)
      switch (error) {
        case "Failed to fetch":
          return "Failed to contact server. Maybe your Internet is down. Maybe my backend is down. Who knows"
        case "1":
          return "A request made to the Bungie API failed unexpectedly. Maybe their servers are down, or maybe there's a bug on my backend. Likely the latter"
        case "2":
          return "An unauthorized request was made."
        case "3":
          return "The Bungie API denied a request I made. I probably have a bug on my backend where I've made the request. Sorry."
        case "4":
          return "My backend is having trouble interpretting data from Bungie. I'll try to fix this. Some day."
        case "5":
          return "A request I made to my backend was formatted incorrectly. Not good."
        case "6":
          return "A request I made to bungie returned a 500 error, meaning this was their fault. Not mine. Try refreshing"
        case "404":
          return "Page not found. *Random text so that this text box is bigger because it looked bad with less text*"
        default:
          return "Something went wrong, and I'm not sure what... Try refreshing, maybe that'll work"
      } 
    }

    return (
      <div className='error'>
        <div className='error-info'>
          <h1 className='uh-oh'> Uh oh... </h1>
          <span className='error-text'> {getErrorText()} </span>
          <button className='button-home' onClick={() => navigate('/')}> Return </button>
        </div>
      </div>
    )
}

export default Error