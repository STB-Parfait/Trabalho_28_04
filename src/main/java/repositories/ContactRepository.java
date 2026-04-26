package repositories;

import config.DatabaseConfig;
import models.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContactRepository {
    public List<Contact> findAll() {
        String sql = "SELECT id, name, email, phone FROM contacts ORDER BY name";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Contact> contacts = new ArrayList<>();
            while (resultSet.next()) {
                contacts.add(mapRow(resultSet));
            }
            return contacts;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not list contacts.", exception);
        }
    }

    public Optional<Contact> findById(long id) {
        String sql = "SELECT id, name, email, phone FROM contacts WHERE id = ?";

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
            throw new IllegalStateException("Could not find contact.", exception);
        }
    }

    public boolean existsById(long id) {
        String sql = "SELECT 1 FROM contacts WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not check contact.", exception);
        }
    }

    public boolean existsByEmail(String email, Long ignoredId) {
        String sql = ignoredId == null
                ? "SELECT 1 FROM contacts WHERE email = ?"
                : "SELECT 1 FROM contacts WHERE email = ? AND id <> ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            if (ignoredId != null) {
                statement.setLong(2, ignoredId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not check email.", exception);
        }
    }

    public Contact create(Contact contact) {
        String sql = "INSERT INTO contacts (name, email, phone) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, contact.getName());
            statement.setString(2, contact.getEmail());
            statement.setString(3, contact.getPhone());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    contact.setId(keys.getLong(1));
                }
            }
            return contact;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not create contact.", exception);
        }
    }

    public Contact update(long id, Contact contact) {
        String sql = "UPDATE contacts SET name = ?, email = ?, phone = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, contact.getName());
            statement.setString(2, contact.getEmail());
            statement.setString(3, contact.getPhone());
            statement.setLong(4, id);
            statement.executeUpdate();
            contact.setId(id);
            return contact;
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not update contact.", exception);
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM contacts WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not delete contact.", exception);
        }
    }

    private Contact mapRow(ResultSet resultSet) throws SQLException {
        Contact contact = new Contact();
        contact.setId(resultSet.getLong("id"));
        contact.setName(resultSet.getString("name"));
        contact.setEmail(resultSet.getString("email"));
        contact.setPhone(resultSet.getString("phone"));
        return contact;
    }
}
