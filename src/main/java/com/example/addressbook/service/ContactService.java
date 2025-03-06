package com.example.addressbook.service;





import com.example.addressbook.dto.ContactDTO;
import com.example.addressbook.model.Contact;
import com.example.addressbook.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private final ContactRepository repository;

    @Autowired
    public ContactService(ContactRepository repository) {
        this.repository = repository;
    }

    // Convert Contact Entity to DTO
    private ContactDTO convertToDTO(Contact contact) {
        return new ContactDTO(contact.getName(), contact.getPhone());
    }

    // Convert DTO to Contact Entity
    private Contact convertToEntity(ContactDTO contactDTO) {
        return new Contact(null, contactDTO.getName(), contactDTO.getPhone());
    }

    // Get All Contacts
    public List<ContactDTO> getAllContacts() {
        return repository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get Contact By ID
    public Optional<ContactDTO> getContactById(Long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    // Add Contact
    public ContactDTO addContact(ContactDTO contactDTO) {
        Contact contact = repository.save(convertToEntity(contactDTO));
        return convertToDTO(contact);
    }

    // Update Contact
    public ContactDTO updateContact(Long id, ContactDTO contactDTO) {
        return repository.findById(id).map(existingContact -> {
            existingContact.setName(contactDTO.getName());
            existingContact.setPhone(contactDTO.getPhone());
            return convertToDTO(repository.save(existingContact));
        }).orElse(null);
    }

    // Delete Contact
    public boolean deleteContact(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}