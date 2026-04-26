package services;

import exceptions.NotFoundException;
import exceptions.ValidationException;
import models.Contact;
import repositories.ContactRepository;

import java.util.List;
import java.util.regex.Pattern;

public class ContactService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    public Contact findById(long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contact not found."));
    }

    public Contact create(Contact contact) {
        validate(contact, null);
        return contactRepository.create(contact);
    }

    public Contact update(long id, Contact contact) {
        findById(id);
        validate(contact, id);
        return contactRepository.update(id, contact);
    }

    public void delete(long id) {
        findById(id);
        contactRepository.delete(id);
    }

    private void validate(Contact contact, Long currentId) {
        if (contact == null) {
            throw new ValidationException("Contact body is required.");
        }
        contact.setName(normalizeRequired(contact.getName(), "Name is required."));
        contact.setEmail(normalizeRequired(contact.getEmail(), "Email is required.").toLowerCase());
        contact.setPhone(normalizeRequired(contact.getPhone(), "Phone is required."));

        if (!EMAIL_PATTERN.matcher(contact.getEmail()).matches()) {
            throw new ValidationException("Email format is invalid.");
        }
        if (contactRepository.existsByEmail(contact.getEmail(), currentId)) {
            throw new ValidationException("Email is already used by another contact.");
        }
    }

    private String normalizeRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
        return value.trim();
    }
}
