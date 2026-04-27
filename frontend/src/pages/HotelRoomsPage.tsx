import { useCallback, useEffect, useState } from "react";
import type { RoomDto } from "../entities/types";
import { roomApi } from "../features/rooms/api";
import { useParams } from "react-router-dom";
import { RoomList } from "../features/rooms/components/RoomList";

export const HotelRoomsPage = () => {
  const { hotelId } = useParams<{ hotelId: string }>();
  const [rooms, setRooms] = useState<RoomDto[]>([]);
  const [loading, setLoading] = useState(true);

  const loadRooms = useCallback(async () => {
    if (!hotelId) {
      setRooms([]);
      setLoading(false);
      return;
    }

    setLoading(true);
    const data = await roomApi.getByHotel(Number(hotelId));
    setRooms(data);
    setLoading(false);
  }, [hotelId]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void loadRooms();
  }, [loadRooms]);

  if (loading) return <div>Загрузка списка номеров...</div>;

  return (
    <div style={{ padding: '20px' }}>
      <h2>Номера отеля</h2>
      {rooms.length > 0 ? (
        <RoomList rooms={rooms} onRoomDeleted={loadRooms} />
      ) : (
        <p>В этом отеле пока нет свободных номеров.</p>
      )}
    </div>
  );
};
