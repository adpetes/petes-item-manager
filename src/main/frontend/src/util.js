import strand from './images/strand.png'
import stasis from './images/stasis.png'
import arc from './images/arc.png'
import solar from './images/solar.png'
import void_ from './images/void.png'
import kinetic from './images/kinetic.png'

export const LocationEnum = {
    EQUIPPED: 1,
    INVENTORY: 2,
    VAULT: 3,
}

export const InventoryItemTypeEnum = {
    kinetic: 1,
    energy: 2,
    power: 3,
    helmet: 4,
    gauntlets: 5,
    chest: 6,
    boots: 7,
    classItem: 8,
}

export const ItemDamageTypeSymbol = {
    "KINETIC": kinetic,
    "STRAND": strand,
    "STASIS": stasis,
    "ARC": arc,
    "SOLAR": solar,
    "VOID": void_
}

export const ErrorCodeEnum = {
    '12': "Cannot transfer equipped items.",
    '1641': "Only one item of this type can be equipped.",
    '1642': "No item spots available.",
    '1623': "Item not found. Try again.",
}