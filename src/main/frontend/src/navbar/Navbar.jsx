import React, { useState } from 'react'
import './navbar.css'
import { FaSearch, FaSync, FaTimesCircle } from 'react-icons/fa';
import { GoSignOut } from 'react-icons/go'
import logo from '../images/logo.png'
import { Link, NavLink, useNavigate } from 'react-router-dom';

function Navbar( props ) {
  const {showSearch, setSearchText, handleRefresh, isRefreshing, handleSignOut} = props
  const [inputValue, setInputValue] = useState('');

  const nav = useNavigate();

  const handleClearInput = () => {
    setInputValue('')
    setSearchText(null)
  }

  const handleChange = (event) => {
    const text = event.target.value;
    setSearchText(text);
    setInputValue(text)
  }

  const refresh = async () => {
    if (isRefreshing){
      return
    }
    await handleRefresh()
  }

  const navStyles = () => {

  }

  return (
    <div className='navbar'>
        <div className='left-container'>
            {showSearch && <GoSignOut className='sign-out' onClick={handleSignOut}/>}
            <img src={logo} className='logo' alt='logo' onClick={() => nav('/')}/>
            <NavLink className={({isActive}) => isActive ? "navbar-link-active": "navbar-link" } exact to='/'>
                Weekly Rotation
              </NavLink>
              <NavLink className={({isActive}) => isActive ? "navbar-link-active": "navbar-link" } to='/inventory'>
                Inventory
              </NavLink>
              <NavLink className={({isActive}) => isActive ? "navbar-link-active": "navbar-link" } to='/about'>
                About PIM
              </NavLink>
        </div>
        <div className={showSearch ? 'middle-container' : 'container-empty'}>
            {showSearch && 
              <>
                <FaSearch className='search-icon'/>
                <input value={inputValue} onChange={handleChange} className='search-bar' type='text' placeholder='Search item name - example: Witherhoard, Fatebringer' />
                <FaTimesCircle className='clear-text-icon' onClick={handleClearInput}/>
                {/* <FaEllipsisV className='ellipsis-icon'/>
                <FaChevronDown className='chevron-down-icon'/> */}
              </>}
              
        </div>
        {showSearch && <div className='right-container'>
            <FaSync className={isRefreshing ? 'spin-animation' : 'sync-icon'} onClick={refresh}/>
            {/* <FaCog className='cog-icon'/> */}
        </div>}
    </div>
  )
}

export default Navbar