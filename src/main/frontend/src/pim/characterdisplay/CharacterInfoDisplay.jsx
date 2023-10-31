import React from 'react'
import './characterinfodisplay.css'
import CharacterContainer from './CharacterContainer'

function CharacterInfoDisplay({ characters }) {
  return (
    characters && <div className='info-container'>
      {characters.map((character) => <CharacterContainer key={character.name} character={character}/>
      )}
    </div>
  )
}

export default CharacterInfoDisplay