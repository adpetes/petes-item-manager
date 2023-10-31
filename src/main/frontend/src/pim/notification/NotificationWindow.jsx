import React from 'react';
import './notification.css'; // Create a CSS file for styling
import Item from '../items/Item';
import { ErrorCodeEnum } from '../../util';
import { FaCheckCircle } from 'react-icons/fa';
import { BsFillXCircleFill } from 'react-icons/bs';
import { AiFillWarning } from 'react-icons/ai';

const NotificationWindow = ({ transferNotiInfo: info, setTransferNotiInfo: setInfo }) => {

  const handleClose = () => {
    setInfo(null);
  };

  const getTransferResult = () => {
    // console.log(info)
    if (info.result.errorCode) {
      const code = info.result.errorCode 
      return ErrorCodeEnum[code] ? ErrorCodeEnum[code] : "Some went wrong. Try again"
    }
    else {
      return "Transferred to " + info.result
    }
  }

  const getResultIconStyles = () => {
    if (info.result.errorCode) {
      return {backgroundColor: 'red', borderColor: 'red'}
    }
    else {
      return {backgroundColor: 'green', borderColor: 'green'}
    }
  }

  const getResultIcon = () => {
    if (!isDemoNoti) {
      return info.result.errorCode ? <BsFillXCircleFill style={getResultIconStyles()} className='result-icon'/> : <FaCheckCircle style={getResultIconStyles()} className='result-icon'/>
    }
  }

  const isDemoNoti = info ? (info.isDemoNoti ? info.isDemoNoti : false) : false

  return (
    info && (
      <div className="notification-window">
        {isDemoNoti ? <AiFillWarning className='warning-icon' /> : <Item data={info}/>}
        <div className="details">
          <span className='item-name'>{isDemoNoti ? "You're in Demo Mode" : info.name}</span>
          <span className='transfer-location'>{isDemoNoti ? "Bungie API requests will not be made" : getTransferResult()}</span>
          <button className="close-button" onClick={handleClose}>
            Dismiss
          </button>
        </div>
        <div className='result-icon-container'>
          {getResultIcon()}
        </div>
        </div>    
    )
  );
};

export default NotificationWindow;
