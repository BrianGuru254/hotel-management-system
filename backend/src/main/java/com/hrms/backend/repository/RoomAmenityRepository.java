package com.hrms.backend.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class RoomAmenityRepository {
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;

    public RoomAmenityRepository(JdbcTemplate jdbc, NamedParameterJdbcTemplate namedJdbc) {
        this.jdbc = jdbc;
        this.namedJdbc = namedJdbc;
    }

    public List<UUID> findAmenityIdsByRoomId(UUID roomId) {
        return jdbc.query("SELECT amenity_id FROM room_amenities WHERE room_id = ? ORDER BY amenity_id",
                (result, row) -> result.getObject(1, UUID.class), roomId);
    }

    public Map<UUID, List<UUID>> findAmenityIdsByRoomIds(Collection<UUID> roomIds) {
        if (roomIds.isEmpty()) return Map.of();
        String sql = "SELECT room_id, amenity_id FROM room_amenities WHERE room_id IN (:roomIds) ORDER BY room_id, amenity_id";
        List<Map.Entry<UUID, UUID>> entries = namedJdbc.query(sql,
                new MapSqlParameterSource("roomIds", roomIds),
                (result, row) -> Map.entry(result.getObject("room_id", UUID.class), result.getObject("amenity_id", UUID.class)));
        Map<UUID, List<UUID>> idsByRoom = new HashMap<>();
        for (Map.Entry<UUID, UUID> entry : entries) {
            idsByRoom.computeIfAbsent(entry.getKey(), ignored -> new ArrayList<>()).add(entry.getValue());
        }
        return idsByRoom;
    }

    public void replaceForRoom(UUID propertyId, UUID roomId, Collection<UUID> amenityIds) {
        jdbc.update("DELETE FROM room_amenities WHERE room_id = ?", roomId);
        for (UUID amenityId : amenityIds) {
            jdbc.update("INSERT INTO room_amenities(property_id, room_id, amenity_id, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)",
                    propertyId, roomId, amenityId);
        }
    }
}
