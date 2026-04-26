package controllers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import models.Contact;
import services.ContactService;

public class ContactHandler extends BaseHandler {
    private final ContactService contactService;

    public ContactHandler(ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        String[] parts = pathParts(exchange, "/contacts");
        String method = method(exchange);

        if (parts.length == 0) {
            switch (method) {
                case "GET" -> sendJson(exchange, 200, contactService.findAll());
                case "POST" -> sendJson(exchange, 201, contactService.create(readJson(exchange, Contact.class)));
                default -> throw new NotFoundException("Route not found.");
            }
            return;
        }

        if (parts.length == 1) {
            long id = parseId(parts[0]);
            switch (method) {
                case "GET" -> sendJson(exchange, 200, contactService.findById(id));
                case "PUT" -> sendJson(exchange, 200, contactService.update(id, readJson(exchange, Contact.class)));
                case "DELETE" -> {
                    contactService.delete(id);
                    sendNoContent(exchange);
                }
                default -> throw new NotFoundException("Route not found.");
            }
            return;
        }

        throw new NotFoundException("Route not found.");
    }
}
