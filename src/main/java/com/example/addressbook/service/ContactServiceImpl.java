package com.example.addressbook.service;

import com.example.addressbook.dto.ContactDTO;
import com.example.addressbook.exception.ResourceNotFoundException;
import com.example.addressbook.model.Contact;
import com.example.addressbook.repository.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactRepository repository;

    @Autowired
    public ContactServiceImpl(ContactRepository repository) {
        this.repository = repository;
    }

    // Convert Contact Entity to DTO
    private ContactDTO convertToDTO(Contact contact) {
        return new ContactDTO(contact.getId(), contact.getName(), contact.getPhone(),contact.getEmail(),contact.getAddress());
    }

    // Convert DTO to Contact Entity
    private Contact convertToEntity(ContactDTO contactDTO) {
        return new Contact(null, contactDTO.getName(), contactDTO.getPhone(),contactDTO.getEmail(),contactDTO.getAddress());
    }


    @Override
    public List<ContactDTO> getAllContacts() {
        return repository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<ContactDTO> getContactById(Long id) {
        Contact contact = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + id));
        return Optional.of(convertToDTO(contact)); // Wrap in Optional
    }

    @Override
    public ContactDTO addContact(ContactDTO contactDTO) {
        Contact contact = repository.save(convertToEntity(contactDTO));
        logger.info("New contact added with ID: {}", contact.getId());
        return convertToDTO(contact);
    }

    @Override
    public ContactDTO updateContact(Long id, ContactDTO contactDTO) {
        Contact existingContact = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + id));

        existingContact.setName(contactDTO.getName());
        existingContact.setPhone(contactDTO.getPhone());
        existingContact.setEmail(contactDTO.getEmail());
        existingContact.setAddress(contactDTO.getAddress());

        return convertToDTO(repository.save(existingContact));
    }

    @Override
    public boolean deleteContact(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Contact not found with ID: " + id);
        }
        repository.deleteById(id);
        logger.info("Contact with ID {} deleted successfully", id);
        return true; // Deletion successful
    }
}