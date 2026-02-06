package karate.integration;

import com.intuit.karate.junit5.Karate;

/**
 * JUnit 5 runner para ejecutar pruebas de integración con Karate DSL.
 * 
 * Este test ejecuta el archivo integration.feature que contiene
 * la prueba de integración end-to-end del sistema bancario.
 * 
 * Para ejecutar:
 * - Todos los tests: mvn test
 * - Solo este test: mvn test -Dtest=IntegrationTest
 * 
 * Requisitos:
 * - Docker Compose debe estar corriendo
 * - ClientApp en http://localhost:8080
 * - TransactionApp en http://localhost:8081
 */
class IntegrationTest {
    
    /**
     * Ejecuta el test de integración definido en integration.feature
     */
    @Karate.Test
    Karate testIntegration() {
        return Karate.run("integration").relativeTo(getClass());
    }
}
