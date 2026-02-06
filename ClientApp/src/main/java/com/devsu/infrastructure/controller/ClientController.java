package com.devsu.infrastructure.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsu.application.dto.ClientRequestDTO;
import com.devsu.application.dto.ClientResponseDTO;
import com.devsu.application.service.ClientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller para gestión de clientes
 * Expone endpoints para operaciones CRUD sobre clientes
 */
@Slf4j
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final ClientService clientService;

    /**
     * Crea un nuevo cliente
     * POST /clientes
     * 
     * @param clientRequestDTO Datos del cliente a crear
     * @return ResponseEntity con el cliente creado y status 201 CREATED
     */
    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(
            @Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        
        log.info("REST request to create client: {}", clientRequestDTO.getClientId());
        ClientResponseDTO response = clientService.createClient(clientRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza un cliente existente
     * PUT /clientes
     * 
     * @param clientRequestDTO Datos del cliente a actualizar
     * @return ResponseEntity con el cliente actualizado y status 200 OK
     */
    @PutMapping
    public ResponseEntity<ClientResponseDTO> updateClient(
            @Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        
        log.info("REST request to update client: {}", clientRequestDTO.getClientId());
        ClientResponseDTO response = clientService.updateClient(clientRequestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina un cliente (soft delete)
     * DELETE /clientes/{id}
     * 
     * @param id ID del cliente a eliminar
     * @return ResponseEntity con el cliente eliminado y status 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> deleteClient(@PathVariable Long id) {
        log.info("REST request to delete client with id: {}", id);
        ClientResponseDTO response = clientService.deleteClient(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un cliente por su ID
     * GET /clientes/{id}
     * 
     * @param id ID del cliente a buscar
     * @return ResponseEntity con el cliente encontrado y status 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClient(@PathVariable Long id) {
        log.info("REST request to get client with id: {}", id);
        ClientResponseDTO response = clientService.getClient(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un cliente por su clientId (para comunicación entre microservicios)
     * GET /clientes/by-clientId/{clientId}
     * 
     * @param clientId ClientId del cliente a buscar
     * @return ResponseEntity con el cliente encontrado y status 200 OK
     */
    @GetMapping("/by-clientId/{clientId}")
    public ResponseEntity<ClientResponseDTO> getClientByClientId(@PathVariable String clientId) {
        log.info("REST request to get client by clientId: {}", clientId);
        ClientResponseDTO response = clientService.getClientByClientId(clientId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los clientes
     * GET /clientes
     * 
     * @return ResponseEntity con lista de clientes y status 200 OK
     */
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        log.info("REST request to get all clients");
        List<ClientResponseDTO> response = clientService.getAllClients();
        return ResponseEntity.ok(response);
    }
    
}
