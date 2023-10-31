import React from 'react'
import './draggableitem.css'
import { ItemDamageTypeSymbol } from '../../util'
import { RxQuestionMarkCircled } from 'react-icons/rx'

function Item({ data, itemRef=null, searchText }) {

  const getImageUrl = () => "https://bungie.net" + data.iconUrl

  const shouldReduceOpacity = () => {
    return searchText && !data.name.toLowerCase().includes(searchText.toLowerCase())
  }

  const itemStyle = {
    opacity: shouldReduceOpacity() ? '0.3' : '1',
  };

  return (
    data ? <div
        className='item'
        ref={itemRef}
        style={itemStyle}
    >
        <img className='item-icon' src={getImageUrl()} alt={data.iconUrl}/>
        <div className='item-info'>
        {ItemDamageTypeSymbol[data.damageType] && <img className='item-damage-type' src={ItemDamageTypeSymbol[data.damageType]} alt={data.damageType} />}
        <span className='item-light'>{data.inventoryItemInstance.light}</span>
        </div>
    </div>
    :
    <div className='item'>
      <RxQuestionMarkCircled size={20} color='gray'/>
    </div>
  )
}

export default Item