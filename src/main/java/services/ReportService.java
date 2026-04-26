package services;

import com.fasterxml.jackson.core.type.TypeReference;
import config.JsonConfig;
import exceptions.AppException;
import models.AgendaSummary;
import models.Appointment;
import models.AppointmentStatus;
import models.Contact;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ReportService {
    private final int port;
    private final HttpClient httpClient;

    public ReportService(int port) {
        this.port = port;
        this.httpClient = HttpClient.newHttpClient();
    }

    public AgendaSummary buildSummary() {
        try {
            List<Contact> contacts = getJson("/contacts", new TypeReference<>() {
            });
            List<Appointment> appointments = getJson("/appointments", new TypeReference<>() {
            });

            AgendaSummary summary = new AgendaSummary();
            summary.setTotalContacts(contacts.size());
            summary.setTotalAppointments(appointments.size());
            summary.setScheduledAppointments(countByStatus(appointments, AppointmentStatus.SCHEDULED));
            summary.setTodayAppointments(countByStatus(appointments, AppointmentStatus.TODAY));
            summary.setPastAppointments(countByStatus(appointments, AppointmentStatus.PAST));
            summary.setCanceledAppointments(countByStatus(appointments, AppointmentStatus.CANCELED));
            return summary;
        } catch (IOException exception) {
            throw new AppException("Could not parse internal API response.", 502);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AppException("Internal API request was interrupted.", 502);
        }
    }

    private <T> T getJson(String path, TypeReference<T> typeReference) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new AppException("Internal API returned HTTP " + response.statusCode() + ".", 502);
        }
        return JsonConfig.mapper().readValue(response.body(), typeReference);
    }

    private int countByStatus(List<Appointment> appointments, AppointmentStatus status) {
        return (int) appointments.stream()
                .filter(appointment -> appointment.getStatus() == status)
                .count();
    }
}
