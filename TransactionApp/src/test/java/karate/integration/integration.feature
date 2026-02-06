Feature: Prueba de Integración - Sistema Bancario Completo
  
  Escenario de prueba de integración que valida el flujo completo:
  1. Crear un cliente en ClientApp
  2. Crear una cuenta asociada al cliente en TransactionApp
  3. Realizar un movimiento (depósito)
  4. Verificar que el saldo se actualizó correctamente
  
  Este test demuestra la integración entre los dos microservicios
  y valida el flujo de negocio end-to-end.

  Background:
    * def clientAppUrl = 'http://localhost:8080/api'
    * def transactionAppUrl = 'http://localhost:8081/api'
    * def timestamp = function(){ return java.lang.System.currentTimeMillis() }
    * def uniqueId = 'TEST_' + timestamp()

  Scenario: Flujo completo - Crear cliente, cuenta, movimiento y verificar saldo
    
    # ========================================
    # 1. CREAR CLIENTE EN CLIENTAPP
    # ========================================
    Given url clientAppUrl
    And path 'clientes'
    And request
      """
      {
        "name": "Cliente Prueba Karate",
        "gender": "Masculino",
        "age": 30,
        "idNumber": "#(uniqueId)",
        "address": "Dirección de prueba",
        "phone": "0999999999",
        "clientId": "#(uniqueId)",
        "password": "test123",
        "state": "true"
      }
      """
    When method POST
    Then status 201
    And match response.clientId == uniqueId
    And match response.name == 'Cliente Prueba Karate'
    And match response.state == 'true'
    * def createdClientId = response.clientId
    * print 'Cliente creado con ID:', createdClientId
    
    # ========================================
    # 2. VERIFICAR QUE EL CLIENTE EXISTE
    # ========================================
    Given url clientAppUrl
    And path 'clientes', response.id
    When method GET
    Then status 200
    And match response.clientId == createdClientId
    * print 'Cliente verificado exitosamente'
    
    # ========================================
    # 3. CREAR CUENTA EN TRANSACTIONAPP
    # ========================================
    Given url transactionAppUrl
    And path 'cuentas'
    And request
      """
      {
        "accountNumber": "#(uniqueId)",
        "accountType": "AHORROS",
        "initialBalance": 1000.0,
        "state": "ACTIVA",
        "clientId": "#(createdClientId)"
      }
      """
    When method POST
    Then status 201
    And match response.accountNumber == uniqueId
    And match response.clientId == createdClientId
    And match response.balance == 1000.0
    And match response.accountType == 'AHORROS'
    And match response.state == 'ACTIVA'
    * def createdAccountId = response.id
    * def initialBalance = response.balance
    * print 'Cuenta creada con ID:', createdAccountId, 'Saldo inicial:', initialBalance
    
    # ========================================
    # 4. VERIFICAR QUE LA CUENTA EXISTE
    # ========================================
    Given url transactionAppUrl
    And path 'cuentas', createdAccountId
    When method GET
    Then status 200
    And match response.id == createdAccountId
    And match response.balance == 1000.0
    * print 'Cuenta verificada exitosamente'
    
    # ========================================
    # 5. CREAR MOVIMIENTO (DEPÓSITO)
    # ========================================
    Given url transactionAppUrl
    And path 'movimientos'
    And request
      """
      {
        "accountId": #(createdAccountId),
        "movementDate": "2026-02-05T10:00:00",
        "movementType": "Deposito de 500 - Prueba Karate",
        "amount": 500.0,
        "state": "ACTIVO"
      }
      """
    When method POST
    Then status 201
    And match response.amount == 500.0
    And match response.balance == 1500.0
    And match response.movementType == 'Deposito de 500 - Prueba Karate'
    And match response.state == 'ACTIVO'
    * def newBalance = response.balance
    * print 'Movimiento creado. Nuevo saldo:', newBalance
    
    # ========================================
    # 6. VERIFICAR SALDO ACTUALIZADO
    # ========================================
    Given url transactionAppUrl
    And path 'cuentas', createdAccountId
    When method GET
    Then status 200
    And match response.balance == 1500.0
    * print 'Saldo verificado correctamente: 1000 + 500 = 1500'
    
    # ========================================
    # 7. CREAR SEGUNDO MOVIMIENTO (RETIRO)
    # ========================================
    Given url transactionAppUrl
    And path 'movimientos'
    And request
      """
      {
        "accountId": #(createdAccountId),
        "movementDate": "2026-02-05T11:00:00",
        "movementType": "Retiro de 300 - Prueba Karate",
        "amount": -300.0,
        "state": "ACTIVO"
      }
      """
    When method POST
    Then status 201
    And match response.amount == -300.0
    And match response.balance == 1200.0
    * print 'Retiro realizado. Nuevo saldo:', response.balance
    
    # ========================================
    # 8. VERIFICAR SALDO FINAL
    # ========================================
    Given url transactionAppUrl
    And path 'cuentas', createdAccountId
    When method GET
    Then status 200
    And match response.balance == 1200.0
    * print 'Saldo final verificado: 1500 - 300 = 1200'
    
    # ========================================
    # 9. OBTENER MOVIMIENTOS DE LA CUENTA
    # ========================================
    Given url transactionAppUrl
    And path 'movimientos'
    And param accountId = createdAccountId
    When method GET
    Then status 200
    And match response == '#array'
    And match response[*].accountId contains createdAccountId
    * print 'Total de movimientos encontrados:', response.length
    
    # ========================================
    # RESUMEN DE LA PRUEBA
    # ========================================
    * print '========================================='
    * print 'PRUEBA DE INTEGRACIÓN COMPLETADA'
    * print '========================================='
    * print 'Cliente ID:', createdClientId
    * print 'Cuenta ID:', createdAccountId
    * print 'Saldo inicial: 1000.0'
    * print 'Depósito: +500.0'
    * print 'Retiro: -300.0'
    * print 'Saldo final: 1200.0'
    * print '========================================='

  # ============================================================================
  # SCENARIO 2: VALIDACIÓN DE SALDO INSUFICIENTE (F3)
  # ============================================================================
  Scenario: Validar error de saldo insuficiente al intentar retiro mayor al disponible
    
    # ========================================
    # 1. CREAR CLIENTE
    # ========================================
    Given url clientAppUrl
    And path 'clientes'
    And request
      """
      {
        "name": "Cliente Saldo Insuficiente",
        "gender": "Femenino",
        "age": 28,
        "idNumber": "#(uniqueId)",
        "address": "Test Address",
        "phone": "0999999999",
        "clientId": "#(uniqueId)",
        "password": "test123",
        "state": "true"
      }
      """
    When method POST
    Then status 201
    * def clientId2 = response.clientId
    * print 'Cliente creado:', clientId2
    
    # ========================================
    # 2. CREAR CUENTA CON SALDO BAJO (100)
    # ========================================
    Given url transactionAppUrl
    And path 'cuentas'
    And request
      """
      {
        "accountNumber": "#(uniqueId + '_ACC')",
        "accountType": "AHORROS",
        "initialBalance": 100.0,
        "state": "ACTIVA",
        "clientId": "#(clientId2)"
      }
      """
    When method POST
    Then status 201
    And match response.balance == 100.0
    * def accountId2 = response.id
    * print 'Cuenta creada con saldo:', response.balance
    
    # ========================================
    # 3. INTENTAR RETIRO MAYOR AL SALDO (200)
    # ========================================
    Given url transactionAppUrl
    And path 'movimientos'
    And request
      """
      {
        "accountId": #(accountId2),
        "movementDate": "2026-02-05T15:00:00",
        "movementType": "Retiro de 200",
        "amount": -200.0,
        "state": "ACTIVO"
      }
      """
    When method POST
    Then status 400
    And match response.message contains 'Saldo no disponible'
    * print 'Error esperado recibido:', response.message
    
    # ========================================
    # 4. VERIFICAR QUE EL SALDO NO CAMBIÓ
    # ========================================
    Given url transactionAppUrl
    And path 'cuentas', accountId2
    When method GET
    Then status 200
    And match response.balance == 100.0
    * print 'Saldo verificado (sin cambios): 100.0'
    
    # ========================================
    # RESUMEN SCENARIO 2
    # ========================================
    * print '========================================='
    * print 'VALIDACIÓN DE SALDO INSUFICIENTE (F3) ✓'
    * print '========================================='
    * print 'Saldo disponible: 100.0'
    * print 'Intento de retiro: 200.0'
    * print 'Resultado: Error 400 - Saldo no disponible'
    * print '========================================='

  # ============================================================================
  # SCENARIO 3: GENERAR REPORTE DE ESTADO DE CUENTA (F4)
  # ============================================================================
  Scenario: Generar reporte de estado de cuenta con movimientos en rango de fechas
    
    # ========================================
    # 1. CREAR CLIENTE
    # ========================================
    Given url clientAppUrl
    And path 'clientes'
    And request
      """
      {
        "name": "Cliente Reporte",
        "gender": "Masculino",
        "age": 40,
        "idNumber": "#(uniqueId)",
        "address": "Reporte Address",
        "phone": "0988888888",
        "clientId": "#(uniqueId)",
        "password": "test123",
        "state": "true"
      }
      """
    When method POST
    Then status 201
    * def clientId3 = response.clientId
    * print 'Cliente creado:', clientId3
    
    # ========================================
    # 2. CREAR CUENTA
    # ========================================
    Given url transactionAppUrl
    And path 'cuentas'
    And request
      """
      {
        "accountNumber": "#(uniqueId + '_REP')",
        "accountType": "CORRIENTE",
        "initialBalance": 2000.0,
        "state": "ACTIVA",
        "clientId": "#(clientId3)"
      }
      """
    When method POST
    Then status 201
    * def accountId3 = response.id
    * print 'Cuenta creada con saldo:', response.balance
    
    # ========================================
    # 3. CREAR VARIOS MOVIMIENTOS
    # ========================================
    # Movimiento 1: Depósito
    Given url transactionAppUrl
    And path 'movimientos'
    And request
      """
      {
        "accountId": #(accountId3),
        "movementDate": "2026-02-01T10:00:00",
        "movementType": "Deposito de 1000",
        "amount": 1000.0,
        "state": "ACTIVO"
      }
      """
    When method POST
    Then status 201
    * print 'Movimiento 1 creado: Depósito +1000'
    
    # Movimiento 2: Retiro
    Given url transactionAppUrl
    And path 'movimientos'
    And request
      """
      {
        "accountId": #(accountId3),
        "movementDate": "2026-02-03T11:00:00",
        "movementType": "Retiro de 500",
        "amount": -500.0,
        "state": "ACTIVO"
      }
      """
    When method POST
    Then status 201
    * print 'Movimiento 2 creado: Retiro -500'
    
    # Movimiento 3: Depósito
    Given url transactionAppUrl
    And path 'movimientos'
    And request
      """
      {
        "accountId": #(accountId3),
        "movementDate": "2026-02-04T12:00:00",
        "movementType": "Deposito de 300",
        "amount": 300.0,
        "state": "ACTIVO"
      }
      """
    When method POST
    Then status 201
    * print 'Movimiento 3 creado: Depósito +300'
    
    # ========================================
    # 4. GENERAR REPORTE DE ESTADO DE CUENTA
    # ========================================
    Given url transactionAppUrl
    And path 'reportes'
    And param clientId = clientId3
    And param startDate = '2026-02-01T00:00:00'
    And param endDate = '2026-02-28T23:59:59'
    When method GET
    Then status 200
    * print 'Reporte generado exitosamente'
    
    # ========================================
    # 5. VALIDAR ESTRUCTURA DEL REPORTE
    # ========================================
    # Validar información del cliente
    And match response.client.clientId == clientId3
    And match response.client.clientName == 'Cliente Reporte'
    * print 'Cliente en reporte:', response.client.clientName
    
    # Validar que tiene cuentas
    And match response.accounts == '#array'
    And match response.accounts[0].accountNumber == uniqueId + '_REP'
    And match response.accounts[0].accountType == 'CORRIENTE'
    * print 'Cuenta en reporte:', response.accounts[0].accountNumber
    
    # Validar movimientos
    And match response.accounts[0].movements == '#array'
    And match response.accounts[0].movements == '#[3]'
    * def movementsCount = response.accounts[0].movements.length
    * print 'Total movimientos en reporte:', movementsCount
    
    # Validar summary
    And match response.summary.totalAccounts == 1
    And match response.summary.totalMovements == 3
    And match response.summary.totalDeposits == 1300.0
    And match response.summary.totalWithdrawals == -500.0
    * print 'Summary - Depósitos:', response.summary.totalDeposits
    * print 'Summary - Retiros:', response.summary.totalWithdrawals
    
    # Validar saldos (el reporte muestra el saldo antes y después de los movimientos en el rango)
    And match response.accounts[0].finalBalance == 2800.0
    * def reportInitialBalance = response.accounts[0].initialBalance
    * print 'Saldo inicial en reporte:', reportInitialBalance
    * print 'Saldo final en reporte:', response.accounts[0].finalBalance
    
    # ========================================
    # RESUMEN SCENARIO 3
    # ========================================
    * print '========================================='
    * print 'REPORTE DE ESTADO DE CUENTA (F4) ✓'
    * print '========================================='
    * print 'Cliente:', clientId3
    * print 'Cuentas en reporte: 1'
    * print 'Movimientos en reporte: 3'
    * print 'Saldo inicial: 2000.0'
    * print 'Total depósitos: +1300.0'
    * print 'Total retiros: -500.0'
    * print 'Saldo final: 2800.0'
    * print '========================================='
