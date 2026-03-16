# Cajero Automático Seguro en Java

Simulación de cajero automático en consola con:

- Autenticación por PIN (4 dígitos)
- Registro completo de movimientos con fecha y saldo posterior
- Uso de `BigDecimal` para precisión monetaria
- Manejo robusto de entradas inválidas
- Código limpio y comentado (Clean Code)

## Características

- PIN de acceso (por defecto: 1234 – solo para práctica)
- Operaciones: consultar saldo, retirar, depositar, ver historial, salir
- Historial persistente en memoria durante la sesión
- Validaciones estrictas (montos > 0, saldo suficiente, etc.)

## Requisitos

- Java 11 o superior (recomendado: Java 17+ o 21)

## Cómo ejecutar

```bash
# Opción 1: desde el IDE (recomendado)
Abrir el proyecto → Run → CajeroAutomaticoSeguroDos.main()
# Opción 2: desde terminal (con javac)
javac -d bin src/main/java/com/jessi/cajero/*.java
