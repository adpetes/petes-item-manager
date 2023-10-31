import React, { useEffect } from 'react'
import { useDrag } from 'react-dnd';
import Item from './Item';

function DraggableItem({ onDragStart, data, location, characterId, searchText }) {
    const [{isDragging}, ref] = useDrag(() => ({
        type: 'ITEM',
        item: { data: data, location: location, characterId: characterId },
        collect: (monitor) => ({
          isDragging: monitor.isDragging(),
        }),
      }));

      useEffect(() => {
        if (isDragging) {
          onDragStart()
        }
      }, [isDragging])
    
      return (
        <Item data={data} itemRef={ref} searchText={searchText}/>
      );
}
export default DraggableItem
