import React, { memo, useCallback, useEffect, useState } from 'react'
import InventorySection from './InventorySection'
import { InventoryItemTypeEnum } from '../../util'
import './inventory.css'
import ItemInfoModal from './iteminfo/ItemInfoModal'

function Inventory(props) {
    const { signOut, setTransferNotiInfo, characters, setErrorMessage, searchText, isDemo } = props

    const [itemInfoVisible, setItemInfoVisible] = useState(false)
    const [itemInfo, setItemInfo] = useState({data: {}, position: {}})
    const [inventorySections, setInventorySections] = useState([])

    const getItemTypes = () => Object.keys(InventoryItemTypeEnum)

    const handleItemInfoClose = () => setItemInfoVisible(false)

    const handleItemClick = (position, data) => {
        console.log(position, data)
        setItemInfoVisible(true)
        setItemInfo({data: data, position: position})
    }

    const eventAction = (e) => {
      if (typeof e.target.className === "string" && !e.target.className.includes("item")) {
        handleItemInfoClose()
      }
    }
    // two bugs: add grenade launcher stats to inventoryItemInstance objects on backend and add enhanced perks to stats

    useEffect(() => {
      document.addEventListener("click", eventAction)
      return () => document.removeEventListener("click", eventAction);
    }, [itemInfoVisible])

    useEffect(() => {
      const sections = getItemTypes().map((itemType) => ({itemType: itemType, data: getInventorySection(itemType)}))
      setInventorySections(sections)
    }, [characters])

    const getInventorySection = (itemType) => {
        let data = []
        for (const character of characters) {
          if (character.characterClass)
          {
            data.push(
                {
                  "class": character.characterClass,
                  "characterId": character.characterId,
                  "equipped": character[itemType + "Equipped"], 
                  "inventory": character[itemType + "InInventory"],
                })
          }
          else {
            data.push(
              {
                "class": "Vault",
                "characterId": 0,
                "inventory": character[itemType]
              }
            )
          }
        }
      return data
    }

    return (
        <div className='inventory'>
            <ItemInfoModal handleClose={handleItemInfoClose} data={itemInfo.data} position={itemInfo.position} visible={itemInfoVisible}/>
            {inventorySections.map((sectionData) => (
              <InventorySection 
                signOut={signOut} 
                setTransferNotiInfo={setTransferNotiInfo} 
                setErrorMessage={setErrorMessage} 
                key={sectionData.itemType} 
                id={sectionData.itemType} 
                data={sectionData.data} 
                searchText={searchText} 
                isDemo={isDemo} 
                handleItemClick={handleItemClick}/>
            ))}
        </div>
    )
}

export default memo(Inventory)