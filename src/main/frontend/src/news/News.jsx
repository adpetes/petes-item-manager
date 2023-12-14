import React, { useEffect, useState } from 'react'
import './news.css'
import ItemInfoModal from '../pim/inventory/iteminfo/ItemInfoModal'
import Navbar from '../navbar/Navbar'
import { getMilestoneRotation } from './service'
import { GridLoader } from 'react-spinners'
import CountdownTimer from './countdown/CountdownTimer'

function News( props ) {
    const { setErrorMessage, handleSignOut } = props

    const [rotationData, setRotationData] = useState(null)
    const [loading, setLoading] = useState(true)
    const [itemInfoVisible, setItemInfoVisible] = useState(false)
    const [itemInfo, setItemInfo] = useState({data: {}, position: {}})

    const handleItemInfoClose = () => setItemInfoVisible(false)

    const handleItemClick = (position, data) => {
        console.log(position, data)
        setItemInfoVisible(true)
        setItemInfo({data: data, position: position})
    }

    useEffect(() => {
        const getRotationData = async () => {
            try {
                const rotationDataRes = await getMilestoneRotation()
                const rotationData = await rotationDataRes.json()
                setRotationData(rotationData)
                console.log("rotation", rotationData)
            } catch (error) {
                setErrorMessage(error.message)
            } finally {
                setLoading(false)
            }
        }
        getRotationData()
    }, [])

    // const eventAction = (e) => {
    //     if (typeof e.target.className === "string" && !e.target.className.includes("item")) {
    //       handleItemInfoClose()
    //     }
    // }
  
    // useEffect(() => {
    //     document.addEventListener("click", eventAction)
    //     return () => document.removeEventListener("click", eventAction);
    // }, [itemInfoVisible])

    return (
        <div>
            <Navbar showSearch={false} handleSignOut={handleSignOut} />
            {loading && <div className='page-loading'>
                <GridLoader
                    color={'white'}
                    size={20}
                    loading={loading} />
                <p className='loading-text'> Retrieving Weekly Rotation Data... </p>
            </div>}
            <ItemInfoModal handleClose={handleItemInfoClose} data={itemInfo.data} position={itemInfo.position} visible={itemInfoVisible}/>
            <div className='news-countdown'> 
                <CountdownTimer/>
            </div>
            {rotationData && <div className='news'>
                {Object.entries(rotationData.data).map(([key, value]) => (
                    <div className='news-item' key={key}>
                        <div className='news-item-header'> 
                            <img className='news-item-header-background' src={`https://bungie.net${value.iconUrl}`} alt={key} />
                            <div className='news-item-header-subtitle'>{key}</div>
                            <div className='news-item-header-title'>{value.name}</div>
                        </div>
                        <div className='news-item-heading'> 
                            Activity Description
                        </div>
                        <div className='news-item-description'> 
                            {value.description}
                        </div>
                        {value.rewards.length !== 0 && 
                            <> 
                                <div className='news-item-heading'> 
                                    Rewards
                                </div>
                                <div className='news-item-rewards'> 
                                    {value.rewards.map((rewardItem) => (
                                        <div 
                                            onMouseEnter={(event) => {
                                                const x = event.clientX + window.scrollX;
                                                const y = event.clientY + window.scrollY + 40;
                                                handleItemClick({x: x, y: y}, rewardItem)
                                            }}
                                            onMouseLeave={handleItemInfoClose}
                                            key={rewardItem.hashVal} 
                                            className='news-item-reward'
                                        >  
                                        <img className='news-item-reward-icon' src={`https://bungie.net${rewardItem.iconUrl}`} />
                                    </div>)
                                    )}
                                </div>
                            </>
                        }
                    </div>
                ))}
            </div>}
        </div>
    )
}

export default News