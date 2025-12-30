package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Reservation;
import com.example.myarena.domain.ReservationStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAOPostgres implements ReservationDAO{

    private static final String URL = "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public Reservation getReservationById(Long id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);){
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToReservation(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Reservation> getReservationByUserId(Long userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToReservation(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Reservation> getReservationByTerrainId(Long terrainId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE terrain_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, terrainId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToReservation(rs));
        } catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void saveReservation(Reservation r) {
        String sql = "INSERT INTO reservations (user_id, terrain_id, start_date, end_date, total_price, status, created_at VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, r.getUserId());
            stmt.setLong(2, r.getTerrainId());
            stmt.setTimestamp(3, new Timestamp(r.getStartDate().getTime()));
            stmt.setTimestamp(4, new Timestamp(r.getEndDate().getTime()));
            stmt.setBigDecimal(5, r.getTotalPrice());
            stmt.setString(6, r.getStatus().name());
            stmt.setTimestamp(7, new Timestamp(r.getCreatedAt().getTime()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()){
                    if (generatedKeys.next()){
                        r.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateReservation(Reservation r) {
        String sql = "UPDATE reservations SET start_date=?, end_date=?, total_price=?, status=? WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(r.getStartDate().getTime()));
            stmt.setTimestamp(2, new Timestamp(r.getEndDate().getTime()));
            stmt.setBigDecimal(3, r.getTotalPrice());
            stmt.setString(4, r.getStatus().name());
            stmt.setLong(5, r.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteReservation(Long id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getLong("id"));
        reservation.setUserId(rs.getLong("user_id"));
        reservation.setTerrainId(rs.getLong("terrain_id"));
        reservation.setStartDate(rs.getTimestamp("start_date"));
        reservation.setEndDate(rs.getTimestamp("end_date"));
        reservation.setTotalPrice(rs.getBigDecimal("total_price"));
        reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
        reservation.setCreatedAt(rs.getTimestamp("created_at"));
        return reservation;
    }

}
