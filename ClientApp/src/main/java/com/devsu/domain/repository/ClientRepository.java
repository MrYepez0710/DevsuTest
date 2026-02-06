package com.devsu.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsu.domain.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    /**
     * Busca un cliente por su clientId único
     * @param clientId ID de negocio del cliente
     * @return Optional con el cliente si existe
     */
    Optional<Client> findByClientId(String clientId);
    
    /**
     * Busca un cliente por su número de identificación
     * @param idNumber Número de identificación de la persona
     * @return Optional con el cliente si existe
     */
    Optional<Client> findByIdNumber(String idNumber);
    
}
