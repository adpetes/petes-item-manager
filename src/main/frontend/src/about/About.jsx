import React from 'react'
import './about.css'
import Navbar from '../navbar/Navbar'
import logo from '../images/logo.png'

function About( props ) {
    const { handleSignOut } = props
    return (
        <div>
            <Navbar showSearch={false} handleSignOut={handleSignOut} />
            <div className='about-page-container'>
                <div className='about-content-container'> 
                    <div className='about-logo'>
                        <img src={logo} className='about-logo-img' alt='logo'/>
                        <div>(Pete's Item Manager)</div>
                    </div>
                    <div className='about-main-content'>
                        <span>Pete's Item Manager, based off the the popular app 'DIM', is an aid to players of the game Destiny 2 - it uses the Bungie API to help players manage their in-game inventories.</span>
                        <h2>Weekly Rotation</h2>
                        <span>The home or "Weekly Rotation" page details events going on in the current week of the game - these activities change week to week. There are more rotating activities in the game that I hope to add in the future, but these are the most popular ones.</span>
                        <h2>Inventory</h2>
                        <span>The "Inventory" page is the main feature of the app. Destiny 2 is a game in which players often have hundreds of items on their accounts, which creates demand for tools which provide utility for tranferring items between an account's characters and storage. Tranferring items is inconvenient to do within the game, so a web app like this one is a big help.</span>
                    </div>
                </div> 
            </div>
        </div>
    )
}

export default About