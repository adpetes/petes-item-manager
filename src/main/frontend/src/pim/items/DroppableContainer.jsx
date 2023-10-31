import React from 'react'
import { useDrop } from 'react-dnd';
import './droppablecontainer.css'

function DroppableContainer( props ) {
    const { children, isValidTarget, location, characterId, onDrop, styles } = props
    const [{ isOver }, ref] = useDrop({
        accept: 'ITEM',
        drop: async (item) => {
            await onDrop(item, location, characterId)
        },
        canDrop: () => isValidTarget
      });

      const getStyles = () => isOver ? {...styles, backgroundColor: 'white'} : styles
    
      return (
        <div
            className="droppable-container"
            ref={ref}
            style={getStyles()}
            >
            {children}
        </div>
      );
}

export default DroppableContainer