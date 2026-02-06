package com.devsu.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Client domain entity (F5)
 * Tests the entity's behavior, inheritance, and business logic
 */
class ClientTest {
    
    private Client client;
    
    @BeforeEach
    void setUp() {
        client = new Client();
    }
    
    /**
     * F5: Test basic client creation and properties
     */
    @Test
    void testClientCreation() {
        // Arrange & Act
        client.setClientId("client-1");
        client.setPassword("1234");
        client.setState("ACTIVO");
        client.setName("José Lema");
        client.setGender("M");
        client.setAge(35);
        client.setIdNumber("1234567890");
        client.setAddress("Otavalo sn y principal");
        client.setPhone("098254785");
        
        // Assert
        assertEquals("client-1", client.getClientId());
        assertEquals("1234", client.getPassword());
        assertEquals("ACTIVO", client.getState());
        assertEquals("José Lema", client.getName());
        assertEquals("M", client.getGender());
        assertEquals(35, client.getAge());
        assertEquals("1234567890", client.getIdNumber());
        assertEquals("Otavalo sn y principal", client.getAddress());
        assertEquals("098254785", client.getPhone());
    }
    
    /**
     * Test client creation with constructor
     */
    @Test
    void testClientCreationWithConstructor() {
        // Arrange & Act
        Client newClient = new Client(
            "Marianela Montalvo",
            "F",
            28,
            "0987654321",
            "Amazonas y NNUU",
            "097548965",
            "client-2",
            "5678",
            "ACTIVO"
        );
        
        // Assert
        assertNotNull(newClient);
        assertEquals("client-2", newClient.getClientId());
        assertEquals("5678", newClient.getPassword());
        assertEquals("ACTIVO", newClient.getState());
        assertEquals("Marianela Montalvo", newClient.getName());
        assertEquals("F", newClient.getGender());
        assertEquals(28, newClient.getAge());
        assertEquals("0987654321", newClient.getIdNumber());
        assertEquals("Amazonas y NNUU", newClient.getAddress());
        assertEquals("097548965", newClient.getPhone());
    }
    
    /**
     * Test client inherits from Person
     */
    @Test
    void testClientInheritsFromPerson() {
        // Arrange & Act
        client.setName("Juan Osorio");
        client.setGender("M");
        client.setAge(40);
        client.setIdNumber("1122334455");
        client.setAddress("13 de junio y equinoccial");
        client.setPhone("098874587");
        
        // Assert - Client should have Person properties
        assertTrue(client instanceof Person);
        assertNotNull(client.getName());
        assertNotNull(client.getGender());
        assertNotNull(client.getAge());
        assertNotNull(client.getIdNumber());
        assertNotNull(client.getAddress());
        assertNotNull(client.getPhone());
    }
    
    /**
     * Test client specific properties
     */
    @Test
    void testClientSpecificProperties() {
        // Arrange & Act
        client.setClientId("client-3");
        client.setPassword("1245");
        client.setState("INACTIVO");
        
        // Assert - Client should have its own properties
        assertNotNull(client.getClientId());
        assertNotNull(client.getPassword());
        assertNotNull(client.getState());
        assertEquals("client-3", client.getClientId());
        assertEquals("1245", client.getPassword());
        assertEquals("INACTIVO", client.getState());
    }
    
    /**
     * Test client state changes
     */
    @Test
    void testClientStateChange() {
        // Arrange
        client.setState("ACTIVO");
        assertEquals("ACTIVO", client.getState());
        
        // Act
        client.setState("INACTIVO");
        
        // Assert
        assertEquals("INACTIVO", client.getState());
    }
    
    /**
     * Test client password update
     */
    @Test
    void testClientPasswordUpdate() {
        // Arrange
        client.setPassword("oldPassword");
        assertEquals("oldPassword", client.getPassword());
        
        // Act
        client.setPassword("newPassword");
        
        // Assert
        assertEquals("newPassword", client.getPassword());
    }
    
    /**
     * Test client equality
     */
    @Test
    void testClientEquality() {
        // Arrange
        Client client1 = new Client();
        client1.setClientId("client-1");
        client1.setPassword("1234");
        client1.setState("ACTIVO");
        client1.setName("José Lema");
        
        Client client2 = new Client();
        client2.setClientId("client-1");
        client2.setPassword("1234");
        client2.setState("ACTIVO");
        client2.setName("José Lema");
        
        // Assert
        assertEquals(client1, client2);
        assertEquals(client1.hashCode(), client2.hashCode());
    }
    
    /**
     * Test client with null values
     */
    @Test
    void testClientWithNullValues() {
        // Arrange & Act
        Client emptyClient = new Client();
        
        // Assert
        assertNull(emptyClient.getClientId());
        assertNull(emptyClient.getPassword());
        assertNull(emptyClient.getState());
        assertNull(emptyClient.getName());
    }
    
    /**
     * Test client serialization (implements Serializable through Person)
     */
    @Test
    void testClientIsSerializable() {
        // Assert
        assertTrue(client instanceof java.io.Serializable);
    }
    
    /**
     * Test complete client data (based on test case from requirements)
     */
    @Test
    void testCompleteClientData() {
        // Arrange & Act - Based on "José Lema" from test requirements
        Client joseClient = new Client(
            "José Lema",
            "M",
            35,
            "1234567890",
            "Otavalo sn y principal",
            "098254785",
            "jose-lema",
            "1234",
            "ACTIVO"
        );
        
        // Assert - Verify all fields are properly set
        assertEquals("jose-lema", joseClient.getClientId());
        assertEquals("1234", joseClient.getPassword());
        assertEquals("ACTIVO", joseClient.getState());
        assertEquals("José Lema", joseClient.getName());
        assertEquals("M", joseClient.getGender());
        assertEquals(35, joseClient.getAge());
        assertEquals("1234567890", joseClient.getIdNumber());
        assertEquals("Otavalo sn y principal", joseClient.getAddress());
        assertEquals("098254785", joseClient.getPhone());
        
        // Verify it's a valid Person
        assertTrue(joseClient instanceof Person);
        
        // Verify it's serializable
        assertTrue(joseClient instanceof java.io.Serializable);
    }
}
