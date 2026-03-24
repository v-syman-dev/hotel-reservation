import type { RoomDto } from "../../../entities/types";
import { roomApi } from "../api";

interface Props {
  rooms: RoomDto[];
  onRoomDeleted: () => void;
}

export const RoomList = ({ rooms, onRoomDeleted }: Props) => {
  const handleDelete = async (id: number) => {
    if (window.confirm('Удалить эту комнату?')) {
      await roomApi.remove(id);
      onRoomDeleted();
    }
  };

  return (
    <div className="room-grid">
      {rooms.map(room => (
        <div key={room.id} className="room-card">
          <h4>Комната №{room.number}</h4>
          <p>Тип: {room.type}</p>
          <p>Цена: ${room.pricePerNight}</p>
          <button onClick={() => handleDelete(room.id!)}>Удалить</button>
        </div>
      ))}
    </div>
  );
};
