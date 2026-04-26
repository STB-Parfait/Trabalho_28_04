package controllers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import models.Appointment;
import services.AppointmentService;

public class AppointmentHandler extends BaseHandler {
    private final AppointmentService appointmentService;

    public AppointmentHandler(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        String[] parts = pathParts(exchange, "/appointments");
        String method = method(exchange);

        if (parts.length == 0) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, appointmentService.findAll());
                case "POST" -> sendJson(exchange, 201, appointmentService.create(readJson(exchange, Appointment.class)));
                default -> throw new NotFoundException("Route not found.");
            }
            return;
        }

        if (parts.length == 1) {
            long id = parseId(parts[0]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, appointmentService.findById(id));
                case "PUT" -> sendJson(exchange, 200, appointmentService.update(id, readJson(exchange, Appointment.class)));
                case "DELETE" -> {
                    appointmentService.delete(id);
                    sendNoContent(exchange);
                }
                default -> throw new NotFoundException("Route not found.");
            }
            return;
        }

        throw new NotFoundException("Route not found.");
    }
}
