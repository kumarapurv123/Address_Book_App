package com.example.addressbook.controller;

import com.example.addressbook.dto.ContactDTO;
import com.example.addressbook.service.ContactService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactControllerTest {

    @Mock
    private ContactService contactService;

    @InjectMocks
    private ContactController contactController;

    /** ✅ Test for Getting All Contacts */
    @Test
    public void testGetAllContacts_ReturnsContacts() {
        ContactDTO contact1 = new ContactDTO(1L, "John Doe", "123-456-7890", "john.doe@example.com", "john street");
        ContactDTO contact2 = new ContactDTO(2L, "Jane Doe", "987-654-3210", "jane.doe@example.com", "jane street");
        List<ContactDTO> contacts = Arrays.asList(contact1, contact2);
        when(contactService.getAllContacts()).thenReturn(contacts);

        ResponseEntity<List<ContactDTO>> response = contactController.getAllContacts();

        assertTrue(response.getStatusCode() == HttpStatus.OK);
        assertTrue(response.getBody() != null);
        assertTrue(response.getBody().size() == 2);
    }

    /** ✅ Test for Getting a Contact by Existing ID */
    @Test
    public void testGetContactById_WithExistingId() {
        ContactDTO contact = new ContactDTO(1L, "Anurag Solanki", "123-456-7890", "john.doe@example.com", "john street");
        when(contactService.getContactById(1L)).thenReturn(Optional.of(contact));

        ResponseEntity<ContactDTO> response = contactController.getContactById(1L);

        assertTrue(response.getStatusCode() == HttpStatus.OK);
        assertTrue(response.getBody() != null);
    }

    /** ✅ Test for Getting a Contact by Non-Existing ID */
    @Test
    public void testGetContactById_WithNonExistingId() {
        when(contactService.getContactById(99L)).thenReturn(Optional.empty());

        ResponseEntity<ContactDTO> response = contactController.getContactById(99L);

        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
        assertFalse(response.hasBody());
    }

    /** ✅ Test for Adding a Contact */
    @Test
    public void testAddContact() {
        ContactDTO newContact = new ContactDTO(null, "New User", "987-654-3210", "new.user@example.com", "new street");
        ContactDTO savedContact = new ContactDTO(1L, "New User", "987-654-3210", "new.user@example.com", "new street");
        when(contactService.addContact(any(ContactDTO.class))).thenReturn(savedContact);

        ResponseEntity<ContactDTO> response = contactController.addContact(newContact);

        assertTrue(response.getStatusCode() == HttpStatus.OK);
        assertTrue(response.getBody() != null);
    }

    /** ✅ Test for Updating a Contact with Existing ID */
    @Test
    public void testUpdateContact_WithExistingId() {
        Long contactId = 1L;
        ContactDTO updatedContactDTO = new ContactDTO(contactId, "Updated Name", "987-654-3210", "updated.name@example.com", "updated street");
        when(contactService.updateContact(contactId, updatedContactDTO)).thenReturn(updatedContactDTO);

        ResponseEntity<ContactDTO> response = contactController.updateContact(contactId, updatedContactDTO);

        assertTrue(response.getStatusCode() == HttpStatus.OK);
        assertTrue(response.getBody() != null);
    }

    /** ✅ Test for Updating a Contact with Non-Existing ID */
    @Test
    public void testUpdateContact_WithNonExistingId() {
        Long contactId = 99L;
        ContactDTO updatedContactDTO = new ContactDTO(contactId, "Updated Name", "987-654-3210", "updated.name@example.com", "updated street");
        when(contactService.updateContact(contactId, updatedContactDTO)).thenReturn(null);

        ResponseEntity<ContactDTO> response = contactController.updateContact(contactId, updatedContactDTO);

        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
        assertFalse(response.hasBody());
    }

    /** ✅ Test for Deleting a Contact with Existing ID */
    @Test
    public void testDeleteContact_WithExistingId() {
        Long contactId = 1L;
        when(contactService.deleteContact(contactId)).thenReturn(true);

        ResponseEntity<Void> response = contactController.deleteContact(contactId);

        assertTrue(response.getStatusCode() == HttpStatus.NO_CONTENT);
        assertFalse(response.hasBody());
    }

    /** ✅ Test for Deleting a Contact with Non-Existing ID */
    @Test
    public void testDeleteContact_WithNonExistingId() {
        Long contactId = 99L;
        when(contactService.deleteContact(contactId)).thenReturn(false);

        ResponseEntity<Void> response = contactController.deleteContact(contactId);

        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND);
        assertFalse(response.hasBody());
    }

    /** ✅ Test for Handling an Exception (Using assertThrows) */
    @Test
    public void testAddContact_ThrowsException() {
        ContactDTO newContact = new ContactDTO(null, "New User", "987-654-3210", "new.user@example.com", "new street");
        when(contactService.addContact(any(ContactDTO.class))).thenThrow(new RuntimeException("Error adding contact"));

        assertThrows(RuntimeException.class, () -> {
            contactController.addContact(newContact);
        });
    }
}