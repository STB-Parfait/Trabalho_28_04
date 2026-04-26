package repositories;

import config.DatabaseConfig;
import models.Appointment;
import models.AppointmentStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentRepository {
    public List<Appointment> findAll() {
        String sql = """
                SELECT id, contact_id, title, description, starts_at, ends_at, status
                FROM appointments
                ORDER BY starts_at
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Appointment> appointments = new ArrayList<>();
            while (resultSet.next()) {
                appointments.add(mapRow(resultSet));
            }
            return appointments;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not list appointments.", exception);
        }
    }

    public Optional<Appointment> findById(long id) {
        String sql = """
                SELECT id, contact_id, title, description, starts_at, ends_at, status
                FROM appointments
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not find appointment.", exception);
        }
    }

    public boolean hasOverlap(long contactId, LocalDateTime startsAt, LocalDateTime endsAt, Long ignoredId) {
        String sql = ignoredId == null
                ? """
                SELECT 1 FROM appointments
                WHERE contact_id = ?
                  AND status <> 'CANCELED'
                  AND ? < ends_at
                  AND ? > starts_at
                """
                : """
                SELECT 1 FROM appointments
                WHERE contact_id = ?
                  AND status <> 'CANCELED'
                  AND id <> ?
                  AND ? < ends_at
                  AND ? > starts_at
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, contactId);
            if (ignoredId == null) {
                statement.setTimestamp(2, Timestamp.valueOf(startsAt));
                statement.setTimestamp(3, Timestamp.valueOf(endsAt));
            } else {
                statement.setLong(2, ignoredId);
                statement.setTimestamp(3, Timestamp.valueOf(startsAt));
                statement.setTimestamp(4, Timestamp.valueOf(endsAt));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not check appointment overlap.", exception);
        }
    }

    public Appointment create(Appointment appointment) {
        String sql = """
                INSERT INTO appointments (contact_id, title, description, starts_at, ends_at, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillStatement(statement, appointment);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    appointment.setId(keys.getLong(1));
                }
            }
            return appointment;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not create appointment.", exception);
        }
    }

    public Appointment update(long id, Appointment appointment) {
        String sql = """
                UPDATE appointments
                SET contact_id = ?, title = ?, description = ?, starts_at = ?, ends_at = ?, status = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, appointment);
            statement.setLong(7, id);
            statement.executeUpdate();
            appointment.setId(id);
            return appointment;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not update appointment.", exception);
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM appointments WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not delete appointment.", exception);
        }
    }

    private void fillStatement(PreparedStatement statement, Appointment appointment) throws SQLException {
        statement.setLong(1, appointment.getContactId());
        statement.setString(2, appointment.getTitle());
        statement.setString(3, appointment.getDescription());
        statement.setTimestamp(4, Timestamp.valueOf(appointment.getStartsAt()));
        statement.setTimestamp(5, Timestamp.valueOf(appointment.getEndsAt()));
        statement.setString(6, appointment.getStatus().name());
    }

    private Appointment mapRow(ResultSet resultSet) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(resultSet.getLong("id"));
        appointment.setContactId(resultSet.getLong("contact_id"));
        appointment.setTitle(resultSet.getString("title"));
        appointment.setDescription(resultSet.getString("description"));
        appointment.setStartsAt(resultSet.getTimestamp("starts_at").toLocalDateTime());
        appointment.setEndsAt(resultSet.getTimestamp("ends_at").toLocalDateTime());
        appointment.setStatus(AppointmentStatus.valueOf(resultSet.getString("status")));
        appointment.setDurationMinutes(appointment.getDurationMinutes());
        return appointment;
    }
}
