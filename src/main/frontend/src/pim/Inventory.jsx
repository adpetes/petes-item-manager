import React, { memo } from 'react'
import InventorySection from './items/InventorySection'
import { InventoryItemTypeEnum } from '../util'

function Inventory(props) {
    const { signOut, setTransferNotiInfo, characters, setErrorMessage, searchText, isDemo } = props

    const getItemTypes = () => Object.keys(InventoryItemTypeEnum)

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
        return <InventorySection signOut={signOut} setTransferNotiInfo={setTransferNotiInfo} setErrorMessage={setErrorMessage} key={itemType} id={itemType} data={data} searchText={searchText} isDemo={isDemo}/>
      }

    return (
        <div className='inventory'>
            {getItemTypes().map((itemType) => {
                return getInventorySection(itemType)
            })}
        </div>
    )
}

export default memo(Inventory)