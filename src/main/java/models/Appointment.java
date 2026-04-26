package models;

import java.time.Duration;
import java.time.LocalDateTime;

public class Appointment {
    private Long id;
    private Long contactId;
    private String title;
    private String description;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private AppointmentStatus status;
    private Long durationMinutes;

    public Appointment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public Long getDurationMinutes() {
        if (durationMinutes != null) {
            return durationMinutes;
        }
        if (startsAt == null || endsAt == null) {
            return null;
        }
        return Duration.between(startsAt, endsAt).toMinutes();
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
