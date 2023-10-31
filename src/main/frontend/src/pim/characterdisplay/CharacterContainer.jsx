import React from 'react'
import { GiDiamonds } from 'react-icons/gi';
import './charcontainer.css'

function CharacterContainer({ character }) {

  const getImageUrl = () => {
    return "https://www.bungie.net" + character.emblem.iconUrl 
  }

  return (
    <div className='character-container'>
      <div className='image-container'>
        <img className='character-emblem' src={getImageUrl()} alt={character.name} />
        <span className='class-text'>
          {character.name}
        </span>
        {character.light && <div className='light-container'>
          <GiDiamonds className='light-icon'/> <span className='light-text'>{character.light}</span>
        </div>}
        {character.light && <span className='title'>Star Baker</span>}
      </div>
    </div>
  )
}
// TODO learn about display and position properties and responsiveness
export default CharacterContainer