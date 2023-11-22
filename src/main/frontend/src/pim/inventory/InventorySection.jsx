import React, { useState, useEffect, useMemo } from 'react'
import { useParams } from 'react-router-dom'
import { DndProvider } from 'react-dnd'
import { HTML5Backend } from 'react-dnd-html5-backend'
import DraggableItem from './draggable/DraggableItem'
import DroppableContainer from './draggable/DroppableContainer'
import { LocationEnum } from '../../util'
import './inventorysection.css'
import Cookies from 'js-cookie'
import { equipItem, transferItem } from '../service'

function InventorySection( props ) {
    const { signOut, setTransferNotiInfo, data, setErrorMessage, searchText, isDemo, handleItemClick } = props
    const params = useParams()
    const [characters, setCharacters] = useState()
    const [isValidTarget, setIsValidTarget] = useState(false)

    useEffect(() => {
      // console.log("updating from data prop", data)
      const sortedData = sortData(data)
      setCharacters(sortedData)
    }, [data])
  
  const sortData = (data) => {
    let sortedData = [...data]
      
    const entries = Object.entries(sortedData[3].inventory);
    entries.sort((a, b) => {
      if (b[1].inventoryItemInstance.light === a[1].inventoryItemInstance.light) {
        return a[1].name.localeCompare(b[1].name);
      }
      return b[1].inventoryItemInstance.light - a[1].inventoryItemInstance.light;
    });
    const sortedVault = Object.fromEntries(entries);
    
    sortedData[3] = {...data[3], inventory: sortedVault}
    return sortedData
  }

  const handleDrop = async (item, targetLocation, targetCharacterId) => {
    setIsValidTarget(false)
    if (targetCharacterId === item.characterId && targetLocation !== LocationEnum.EQUIPPED) {
      return
    }
    if (item.location === LocationEnum.EQUIPPED) {
      setTransferNotiInfo({...item.data, result: {errorCode: "12"}})
      return
    }

    let updatedCharacters = [...characters]
    let sourceCharacter = updatedCharacters.find(character => character.characterId === item.characterId)
    let targetCharacter = updatedCharacters.find(character => character.characterId === targetCharacterId)
    console.log("transfer request: ", sourceCharacter.class, targetCharacter.class)

    const itemId = item.data.inventoryItemInstance.instanceItemId

    const sessionToken = !isDemo ? Cookies.get("sessionToken") : process.env.REACT_APP_DEMO_TOKEN;
    const accountId = !isDemo ? Cookies.get("accountId") : process.env.REACT_APP_DEMO_ACCOUNT_ID;
    const {profileId, membershipType} = params
    if (sessionToken && accountId) {
      let response = null;
        try {
          if (targetLocation === LocationEnum.EQUIPPED) {
            // TODO handle equipItem case - transfer FROM equipped 
            response = await equipItem(sessionToken, accountId, profileId, membershipType, item.data, item.characterId, targetCharacterId, isDemo)
            if (response.ok) {
              const curEquipped = targetCharacter.equipped
              targetCharacter.inventory[curEquipped.inventoryItemInstance.instanceItemId] = curEquipped
              targetCharacter.equipped = item.data
            }
          }
          else {
            if (targetCharacterId === 0) {
              response = await transferItem(sessionToken, accountId, profileId, membershipType, item.data, item.characterId, "0", true, isDemo)
            }
            else {
              response = await transferItem(sessionToken, accountId, profileId, membershipType, item.data, item.characterId, targetCharacterId, false, isDemo)
            }
            if (response.ok) {
              targetCharacter.inventory[itemId] = item.data
            }
          }
        } catch (error) {
            console.log(error.message)
            const code = error.message
            if (code === "0" || code === "1" || code === "2" || code === "Failed to fetch") {
              setErrorMessage(code)
            }
            setTransferNotiInfo({...item.data, result: {errorCode: code}})
            return
          }
        if (response.ok) {
          delete sourceCharacter.inventory[itemId]
          setCharacters(sortData(updatedCharacters))
          setTransferNotiInfo({...item.data, result: targetCharacter.class})
        }
    }
    else {
        await signOut()
    }
  };

  const onDragStart = () => {
    setIsValidTarget(true)
  }

  const getCharactersWithoutVault = () => {
    const charsNoVault = []

    for (const character of characters) {
      if (character.characterId !== 0) {
        charsNoVault.push(character)
      }
    }
    return charsNoVault
  }

  const getVault = () => {
    for (const character of characters) {
      if (character.characterId === 0) {
        return character
      }
    }
  }

  const stylesUnequipped = {
    display: 'grid',
    gridTemplateColumns: 'repeat(3, 1fr)',
    minWidth: '164px'
  };

  const stylesEquipped = {
    marginRight: '15px',
  }

  const stylesVault = {
    display: 'flex',
    flexWrap: 'wrap',
    marginLeft: '15px',
    flex: 1,
  }

  const getStyles = (styles) => {
    return isValidTarget ? {...styles, backgroundColor: '#383855'} : styles
  }

  return (
    characters && <DndProvider backend={HTML5Backend}>
        <div className='inventory-section'>
          {getCharactersWithoutVault().map((character) => (
            <div key={character.characterId} className='inventory-section-characters'> 
              <DroppableContainer isValidTarget={isValidTarget} location={LocationEnum.EQUIPPED} characterId={character.characterId} onDrop={handleDrop} styles={getStyles(stylesEquipped)}>
                <DraggableItem onDragStart={onDragStart} data={character.equipped} location={LocationEnum.EQUIPPED} characterId={character.characterId} searchText={searchText} handleClick={handleItemClick}/>
              </DroppableContainer>
              <DroppableContainer isValidTarget={isValidTarget} key={character.characterId} location={LocationEnum.INVENTORY} characterId={character.characterId} onDrop={handleDrop} styles={getStyles(stylesUnequipped)}>
                {Object.keys(character.inventory).map((itemKey) => (
                    <DraggableItem onDragStart={onDragStart} key={itemKey} data={character.inventory[itemKey]} characterId={character.characterId} searchText={searchText} handleClick={handleItemClick}/>
                ))}
              </DroppableContainer>
            </div>
          ))}
          <DroppableContainer isValidTarget={isValidTarget} location={LocationEnum.VAULT} characterId={getVault().characterId} onDrop={handleDrop} styles={getStyles(stylesVault)}>
            {Object.keys(getVault().inventory).map((itemKey) => (
                <DraggableItem onDragStart={onDragStart} key={itemKey} location={LocationEnum.VAULT} characterId={getVault().characterId} data={getVault().inventory[itemKey]} searchText={searchText} handleClick={handleItemClick} />
            ))}
          </DroppableContainer>
        </div>
    </DndProvider>
  )
}

export default InventorySection