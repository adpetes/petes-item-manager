import React, { useEffect, useState } from 'react'
import './news.css'
import { Link, Navigate } from 'react-router-dom'
import Navbar from '../navbar/Navbar'
import { getMilestoneRotation } from './service'
import { GridLoader } from 'react-spinners'
import Item from '../pim/inventory/draggable/Item'
import CountdownTimer from './countdown/CountdownTimer'

function News( props ) {
    const { setErrorMessage, handleSignOut } = props

    const [rotationData, setRotationData] = useState(null)
    const [loading, setLoading] = useState(true)

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
                                    {value.rewards.map((rewardItem) => (<div key={rewardItem.hashVal} className='news-item-reward'>  
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