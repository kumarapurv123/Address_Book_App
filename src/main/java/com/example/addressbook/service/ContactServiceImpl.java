package com.example.addressbook.service;

import com.example.addressbook.dto.ContactDTO;
import com.example.addressbook.exception.ResourceNotFoundException;
import com.example.addressbook.model.Contact;
import com.example.addressbook.repository.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ContactServiceImpl(ContactRepository repository, RedisTemplate<String, Object> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    // Convert Contact Entity to DTO
    private ContactDTO convertToDTO(Contact contact) {
        return new ContactDTO(contact.getId(), contact.getName(), contact.getPhone(), contact.getEmail(), contact.getAddress());
    }

    // Convert DTO to Contact Entity
    private Contact convertToEntity(ContactDTO contactDTO) {
        return new Contact(null, contactDTO.getName(), contactDTO.getPhone(), contactDTO.getEmail(), contactDTO.getAddress());
    }

    @Override
    public List<ContactDTO> getAllContacts() {
        String cacheKey = "contacts:all";
        List<ContactDTO> cachedContacts = (List<ContactDTO>) redisTemplate.opsForValue().get(cacheKey);

        if (cachedContacts != null) {
            logger.info("Fetching contacts from cache");
            return cachedContacts;
        }

        List<ContactDTO> contacts = repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(cacheKey, contacts, 1, TimeUnit.HOURS); // Cache for 1 hour
        logger.info("Caching contacts");

        return contacts;
    }

    @Override
    public Optional<ContactDTO> getContactById(Long id) {
        String cacheKey = "contact:" + id;
        ContactDTO cachedContact = (ContactDTO) redisTemplate.opsForValue().get(cacheKey);

        if (cachedContact != null) {
            logger.info("Fetching contact from cache for ID: {}", id);
            return Optional.of(cachedContact);
        }

        Contact contact = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + id));

        redisTemplate.opsForValue().set(cacheKey, convertToDTO(contact), 1, TimeUnit.HOURS); // Cache for 1 hour
        logger.info("Caching contact for ID: {}", id);

        return Optional.of(convertToDTO(contact));
    }

    @Override
    public ContactDTO addContact(ContactDTO contactDTO) {
        Contact contact = repository.save(convertToEntity(contactDTO));
        logger.info("New contact added with ID: {}", contact.getId());

        // Invalidate the cache for getAllContacts
        redisTemplate.delete("contacts:all");

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

        ContactDTO updatedContact = convertToDTO(repository.save(existingContact));

        // Invalidate the cache for the specific contact and getAllContacts
        redisTemplate.delete("contact:" + id);
        redisTemplate.delete("contacts:all");

        return updatedContact;
    }

    @Override
    public boolean deleteContact(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Contact not found with ID: " + id);
        }

        repository.deleteById(id);
        logger.info("Contact with ID {} deleted successfully", id);

        // Invalidate the cache for the specific contact and getAllContacts
        redisTemplate.delete("contact:" + id);
        redisTemplate.delete("contacts:all");

        return true;
    }
}