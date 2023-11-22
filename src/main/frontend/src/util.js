import strand from './images/strand.png'
import stasis from './images/stasis.png'
import arc from './images/arc.png'
import solar from './images/solar.png'
import void_ from './images/void.png'
import kinetic_dmg_type from './images/kinetic.png'
import primary from './images/primary.png'
import special from './images/special.png'
import power from './images/power.png'

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
    "KINETIC": kinetic_dmg_type,
    "STRAND": strand,
    "STASIS": stasis,
    "ARC": arc,
    "SOLAR": solar,
    "VOID": void_
}

export const ItemSubTypeSymbol = {
    "Auto Rifle": primary,
    "Hand Cannon": primary,
    "Pulse Rifle": primary,
    "Scout Rifle": primary,
    "Side Arm": primary,
    "Submachine Gun": primary,
    "Bow": primary,
    "Shotgun": special,
    "Fusion Rifle": special,
    "Sniper Rifle": special,
    "Grenade Launcher": special,
    "Glaive": special,
    "Linear Fusion Rifle": special,
    "Machine Gun": power,
    "Rocket Launcher": power,
    "Sword": power,
}

export const ErrorCodeEnum = {
    '12': "Cannot transfer equipped items.",
    '1641': "Only one item of this type can be equipped.",
    '1642': "No item spots available.",
    '1623': "Item not found. Try again.",
}

export const statOrdering = ['RPM', 'Blast Radius', 'Velocity', 'Draw Time', 'Swing Speed', 'Impact', 'Guard Resistance', 'Charge Rate', 'Guard Endurance', 'Ammo Capacity', 'Accuracy', 'Range', 'Shield Duration', 'Stability', 'Handling', 'Reload Speed', 'Aim Assistance', 'Airborne', 'Zoom', 'Recoil Direction', 'Magazine']
