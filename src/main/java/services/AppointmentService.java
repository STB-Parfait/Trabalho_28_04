package services;

import exceptions.NotFoundException;
import exceptions.ValidationException;
import models.Appointment;
import models.AppointmentStatus;
import repositories.AppointmentRepository;
import repositories.ContactRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ContactRepository contactRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, ContactRepository contactRepository) {
        this.appointmentRepository = appointmentRepository;
        this.contactRepository = contactRepository;
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll().stream()
                .map(this::withCalculatedStatus)
                .toList();
    }

    public Appointment findById(long id) {
        return appointmentRepository.findById(id)
                .map(this::withCalculatedStatus)
                .orElseThrow(() -> new NotFoundException("Appointment not found."));
    }

    public Appointment create(Appointment appointment) {
        validate(appointment, null);
        appointment.setStatus(normalizeStatus(appointment.getStatus()));
        return withCalculatedStatus(appointmentRepository.create(appointment));
    }

    public Appointment update(long id, Appointment appointment) {
        findById(id);
        validate(appointment, id);
        appointment.setStatus(normalizeStatus(appointment.getStatus()));
        return withCalculatedStatus(appointmentRepository.update(id, appointment));
    }

    public void delete(long id) {
        findById(id);
        appointmentRepository.delete(id);
    }

    private void validate(Appointment appointment, Long currentId) {
        if (appointment == null) {
            throw new ValidationException("Appointment body is required.");
        }
        if (appointment.getContactId() == null) {
            throw new ValidationException("contactId is required.");
        }
        if (!contactRepository.existsById(appointment.getContactId())) {
            throw new ValidationException("The informed contact does not exist.");
        }

        appointment.setTitle(normalizeRequired(appointment.getTitle(), "Title is required."));
        if (appointment.getStartsAt() == null) {
            throw new ValidationException("startsAt is required.");
        }
        if (appointment.getEndsAt() == null) {
            throw new ValidationException("endsAt is required.");
        }
        if (!appointment.getStartsAt().isBefore(appointment.getEndsAt())) {
            throw new ValidationException("startsAt must be before endsAt.");
        }

        AppointmentStatus status = normalizeStatus(appointment.getStatus());
        if (status != AppointmentStatus.CANCELED
                && appointmentRepository.hasOverlap(
                appointment.getContactId(),
                appointment.getStartsAt(),
                appointment.getEndsAt(),
                currentId)) {
            throw new ValidationException("This contact already has an appointment in the informed period.");
        }
    }

    private AppointmentStatus normalizeStatus(AppointmentStatus status) {
        if (status == null || status == AppointmentStatus.TODAY || status == AppointmentStatus.PAST) {
            return AppointmentStatus.SCHEDULED;
        }
        return status;
    }

    private Appointment withCalculatedStatus(Appointment appointment) {
        if (appointment.getStatus() != AppointmentStatus.CANCELED) {
            appointment.setStatus(calculateStatus(appointment));
        }
        appointment.setDurationMinutes(appointment.getDurationMinutes());
        return appointment;
    }

    private AppointmentStatus calculateStatus(Appointment appointment) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        if (appointment.getEndsAt().isBefore(now)) {
            return AppointmentStatus.PAST;
        }
        if (appointment.getStartsAt().toLocalDate().equals(today)) {
            return AppointmentStatus.TODAY;
        }
        return AppointmentStatus.SCHEDULED;
    }

    private String normalizeRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
        return value.trim();
    }
}
