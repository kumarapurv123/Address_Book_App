package com.example.addressbook.controller;

import com.example.addressbook.dto.ContactDTO;
import com.example.addressbook.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contacts", description = "Operations related to contacts")
public class ContactController {

    private final ContactService service;

    @Autowired
    public ContactController(ContactService service) {
        this.service = service;
    }

    // GET All Contacts
    @GetMapping
    @Operation(summary = "Get all contacts")
    public ResponseEntity<List<ContactDTO>> getAllContacts() {
        return ResponseEntity.ok(service.getAllContacts());
    }

    // GET Contact by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get contact by ID")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long id) {
        Optional<ContactDTO> contact = service.getContactById(id);
        return contact.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Add New Contact
    @PostMapping
    @Operation(summary = "Add a new contact")
    public ResponseEntity<ContactDTO> addContact(@RequestBody ContactDTO contactDTO) {
        return ResponseEntity.ok(service.addContact(contactDTO));
    }

    // PUT - Update Contact by ID
    @PutMapping("/{id}")
    @Operation(summary = "Update a contact by ID")
    public ResponseEntity<ContactDTO> updateContact(@PathVariable Long id, @RequestBody ContactDTO contactDTO) {
        ContactDTO updatedContact = service.updateContact(id, contactDTO);
        return updatedContact != null ? ResponseEntity.ok(updatedContact)
                : ResponseEntity.notFound().build();
    }

    // DELETE - Remove Contact by ID
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a contact by ID")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        return service.deleteContact(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}