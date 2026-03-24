import { useEffect, useState } from "react";
import type { RoomDto } from "../entities/types";
import { roomApi } from "../features/rooms/api";
import { useParams } from "react-router-dom";
import { RoomList } from "../features/rooms/components/RoomList";

export const HotelRoomsPage = () => {
  const { hotelId } = useParams<{ hotelId: string }>();
  const [rooms, setRooms] = useState<RoomDto[]>([]);
  const [loading, setLoading] = useState(true);

  const loadRooms = async () => {
    if (hotelId) {
      const data = await roomApi.getByHotel(Number(hotelId));
      setRooms(data);
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRooms();
  }, [hotelId]);

  if (loading) return <div>Загрузка списка номеров...</div>;

  return (
    <div style={{ padding: '20px' }}>
      <h2>Номера отеля #{hotelId}</h2>
      {rooms.length > 0 ? (
        <RoomList rooms={rooms} onRoomDeleted={loadRooms} />
      ) : (
        <p>В этом отеле пока нет свободных номеров.</p>
      )}
    </div>
  );
};
