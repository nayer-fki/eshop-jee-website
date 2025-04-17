package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PromotionalVideoDB {

    /**
     * Retrieves the current promotional video URL from the database.
     *
     * @return The PromotionalVideo_model object, or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public PromotionalVideo_model getPromotionalVideo() throws SQLException {
        String sql = "SELECT * FROM promotional_video LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                PromotionalVideo_model video = new PromotionalVideo_model();
                video.setId(rs.getInt("id"));
                video.setVideoUrl(rs.getString("video_url"));
                return video;
            }
        }
        return null;
    }

    /**
     * Updates the promotional video URL in the database.
     *
     * @param video The PromotionalVideo_model object with the new URL.
     * @throws SQLException If a database error occurs.
     */
    public void updatePromotionalVideo(PromotionalVideo_model video) throws SQLException {
        String sql = "UPDATE promotional_video SET video_url = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, video.getVideoUrl());
            pstmt.setInt(2, video.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                // If no rows were updated, the video might not exist; insert a new one
                insertPromotionalVideo(video);
            }
        }
    }

    /**
     * Inserts a new promotional video URL into the database.
     *
     * @param video The PromotionalVideo_model object to insert.
     * @throws SQLException If a database error occurs.
     */
    private void insertPromotionalVideo(PromotionalVideo_model video) throws SQLException {
        String sql = "INSERT INTO promotional_video (video_url) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, video.getVideoUrl());
            pstmt.executeUpdate();
        }
    }
}