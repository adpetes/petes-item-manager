import React from 'react'
import './iteminfomodal.css'
import { ItemDamageTypeSymbol, ItemSubTypeSymbol, statOrdering } from '../../../util'

function ItemInfoModal( props ) {
    const { data, position, handleClose, visible } = props

    const getCleanStatsData = () => {
        const stats = data.inventoryItemInstance.stats
        // const {['RPM']: _, ...cleanedStats} = stats
        // let cleanedStats = stats.filter((stat) => (stat.statType != 'RPM'))
        
        const reorderedStats = stats.sort((a, b) => (statOrdering.indexOf(a.statType) - statOrdering.indexOf(b.statType)))
        return reorderedStats
    }

    const styles = {
        top: `${position.y - 120}px`,
        left: `${position.x}px`,
    }

    const getAmmoType = () => {
        if (data.damageType === "POWER"){
            if (data.itemSubType === "Grenade Launcher" || data.itemSubType === "Linear Fusion Rifle") {
                return ItemSubTypeSymbol["Sword"]
            }
        }
        return ItemSubTypeSymbol[data.itemSubType]
    }

    return (
        visible && <div id='modal' style={styles} className='item-info-modal'>
            {/* <button onClick={handleClose}>close</button> */}
            <div className='item-info-header'>
                <div className='item-info-header-name'>{data.name.toUpperCase()}</div>
                <div className='item-info-header-bottom'>
                    <div className='item-info-item-type'>
                        <div className='item-info-sub-type'>{data.itemSubType}</div>
                        {ItemSubTypeSymbol[data.itemSubType] && <img className='item-info-ammo-type' src={getAmmoType()} alt={data.itemSubType} />}
                    </div>
                    <div className='item-info-light'>
                        {ItemDamageTypeSymbol[data.damageType] && <img className='item-info-damage-type' src={ItemDamageTypeSymbol[data.damageType]} alt={data.damageType} />}
                        {data.inventoryItemInstance.light}
                    </div>
                </div>
            </div>
            <div className='item-info-body'>
                <div className='item-info-stats'>
                    {getCleanStatsData().map((stat) => {
                        return <div key={stat.statType} className='item-info-stat-section'>
                            <div className='item-info-stat-name'>{stat.statType}</div>
                            <div className='item-info-stat-value'>{stat.statValue}</div>
                            {(stat.statType !== 'RPM' && stat.statType !== 'Draw Time' && stat.statType !== 'Charge Time') && <div className='item-info-stat-bar'>
                                <div style={{width: `${stat.statValue}%`}} className='item-info-stat-bar-fill'></div>
                            </div>}
                        </div>
                    })}
                </div>
                <div className='item-info-perks'>
                    {data.inventoryItemInstance.perk.map((perk) => (
                        <svg key={perk.hashVal} viewBox="0 0 100 100" className='item-info-perk-img'>
                            <defs>
                                <linearGradient id="mw" x1="0" x2="0" y1="0" y2="1">
                                    <stop stopColor="#eade8b" offset="50%" stopOpacity="0"></stop>
                                    <stop stopColor="#eade8b" offset="100%" stopOpacity="1"></stop>
                                </linearGradient>
                            </defs>
                            <mask id="mask">
                                <circle cx="50" cy="50" r="46" fill="white"></circle>
                            </mask>
                            <circle cx="50" cy="50" r="48"></circle>
                            <circle cx="50" cy="50" r="48" fill='#4887ba'></circle>
                            <image href={"https://www.bungie.net/" + perk.iconUrl} x="10" y="10" width="80" height="80" mask="url(#mask)"></image>
                            <circle cx="50" cy="50" r="46" stroke="white" fill="transparent" strokeWidth="2"></circle>
                        </svg>
                        // <img className='item-info-perk-img' key={perk.hashVal} alt={perk.hashVal} src={"https://www.bungie.net" + perk.iconUrl} />
                    ))}
                </div>
            </div>
        </div>
    )
}

export default ItemInfoModal