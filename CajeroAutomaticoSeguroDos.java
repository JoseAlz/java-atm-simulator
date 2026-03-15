package com.jessi.cajero;// ← Paquete convencional y descriptivo

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
/**
 * Simulación de Cajero Automático con autenticación PIN y registro de movimientos.
 * Proyecto de práctica en Java (Clean Code + buenas prácticas 2025–2026).
 *
 * @author javai
 * @version 1.0
 */

public class CajeroAutomaticoSeguroDos {

    // ────────────────────────────────────────────────
    // Constantes de configuración
    // ────────────────────────────────────────────────
    private static final String PIN_CORRECTO = "1234";  // ¡Solo para demo! En prod: hash + salt
    private static final BigDecimal SALDO_INICIAL = new BigDecimal("1000.00");
    private static final BigDecimal CERO = BigDecimal.ZERO;
    private static final int ESCALA_DINERO = 2;
    private static final int MAX_INTENTOS_PIN = 3;

    // ────────────────────────────────────────────────
    // Campos
    // ────────────────────────────────────────────────
    private final Scanner consola;
    private BigDecimal saldo;
    private final List<Movimiento> historial;
    private boolean autenticado;
    private boolean salir;

    // ────────────────────────────────────────────────
    // Clase interna para registrar movimientos
    // ────────────────────────────────────────────────
    private static class Movimiento {
        final LocalDateTime fecha;
        final String tipo;
        final BigDecimal monto;
        final BigDecimal saldoPosterior;

        Movimiento(String tipo, BigDecimal monto, BigDecimal saldoPosterior) {
            this.fecha = LocalDateTime.now();
            this.tipo = tipo;
            this.monto = monto;
            this.saldoPosterior = saldoPosterior;
        }

        @Override
        public String toString() {
            String montoStr = monto.compareTo(CERO) == 0
                    ? ""
                    : String.format(" %s %s",
                    tipo.equals("Depósito") ? "+" : "-",
                    formatearDinero(monto.abs()));

            return String.format("%s | %-12s%s | Saldo: %s",
                    fecha.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")),
                    tipo,
                    montoStr,
                    formatearDinero(saldoPosterior));
        }
    }

    // ────────────────────────────────────────────────
    // Constructor principal (el que se debe usar)
    // ────────────────────────────────────────────────
    public CajeroAutomaticoSeguroDos() {
        this.consola = new Scanner(System.in);
        this.saldo = SALDO_INICIAL;
        this.historial = new ArrayList<>();
        this.autenticado = false;
        this.salir = false;

        // Movimiento inicial de apertura
        historial.add(new Movimiento("Apertura cuenta", CERO, saldo));
    }

    // Constructor para testing o inyección (opcional)
    public CajeroAutomaticoSeguroDos(Scanner consola, List<Movimiento> historial) {
        this.consola = consola;
        this.historial = historial != null ? historial : new ArrayList<>();
        this.saldo = SALDO_INICIAL;
        this.autenticado = false;
        this.salir = false;
    }

    // ────────────────────────────────────────────────
    // Punto de entrada principal
    // ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("Iniciando Cajero Automático Seguro...");
        CajeroAutomaticoSeguroDos cajero = new CajeroAutomaticoSeguroDos();  // ← usa el constructor sin parámetros
        cajero.iniciar();
    }

    private void iniciar() {
        mostrarBienvenida();

        if (!autenticarConPin()) {
            System.out.println("\n¡Tarjeta retenida! Contacte a su banco.");
            return;
        }

        autenticado = true;
        System.out.println("\n¡Acceso concedido! Bienvenido.");

        while (!salir) {
            mostrarMenu();
            int opcion = leerOpcion();
            procesarOpcion(opcion);
        }

        mostrarDespedida();
        consola.close();
    }

    // ────────────────────────────────────────────────
    // Métodos de autenticación
    // ────────────────────────────────────────────────
    private boolean autenticarConPin() {
        int intentos = 0;
        while (intentos < MAX_INTENTOS_PIN) {
            System.out.print("\nIngrese PIN (4 dígitos): ");
            String pin = consola.nextLine().trim();

            if (PIN_CORRECTO.equals(pin)) {
                return true;
            }

            intentos++;
            System.out.printf("PIN incorrecto. %d intento%s restante%s.%n",
                    MAX_INTENTOS_PIN - intentos,
                    (MAX_INTENTOS_PIN - intentos == 1) ? "" : "s",
                    (MAX_INTENTOS_PIN - intentos == 1) ? "" : "s");
        }
        return false;
    }

    // ────────────────────────────────────────────────
    // Interfaz de usuario
    // ────────────────────────────────────────────────
    private void mostrarBienvenida() {
        lineaDecorativa(60);
        System.out.println("     CAJERO AUTOMÁTICO - BANCO DIGITAL     ");
        lineaDecorativa(60);
    }

    private void mostrarMenu() {
        System.out.println("\nMenú principal:");
        System.out.println("  1. Consultar saldo");
        System.out.println("  2. Retirar dinero");
        System.out.println("  3. Depositar dinero");
        System.out.println("  4. Ver historial de movimientos");
        System.out.println("  5. Salir");
        System.out.print("\n→ Opción (1-5): ");
    }

    private void mostrarDespedida() {
        lineaDecorativa(60);
        System.out.println("         SESIÓN FINALIZADA         ");
        lineaDecorativa(60);
    }

    private static void lineaDecorativa(int longitud) {
        System.out.println("=".repeat(longitud));
    }

    // ────────────────────────────────────────────────
    // Lógica de entrada y procesamiento
    // ────────────────────────────────────────────────
    private int leerOpcion() {
        try {
            int opcion = consola.nextInt();
            consola.nextLine(); // limpiar buffer
            return opcion;
        } catch (InputMismatchException e) {
            consola.nextLine();
            return -1;
        }
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> consultarSaldo();
            case 2 -> retirar();
            case 3 -> depositar();
            case 4 -> mostrarHistorial();
            case 5 -> salir();
            default -> {
                System.out.println("\nOpción inválida. Elija entre 1 y 5.");
                pausar();
            }
        }
    }

    private void consultarSaldo() {
        registrarMovimiento("Consulta", CERO);
        System.out.printf("\nSaldo disponible: %s%n", formatearDinero(saldo));
        pausar();
    }

    private void retirar() {
        BigDecimal monto = leerMonto("Monto a retirar: ");
        if (monto == null || montoInvalido(monto)) return;

        if (monto.compareTo(saldo) > 0) {
            System.out.printf("Saldo insuficiente. Disponible: %s%n", formatearDinero(saldo));
            pausar();
            return;
        }

        saldo = saldo.subtract(monto);
        registrarMovimiento("Retiro", monto);
        System.out.printf("Retiro OK → Nuevo saldo: %s%n", formatearDinero(saldo));
        pausar();
    }

    private void depositar() {
        BigDecimal monto = leerMonto("Monto a depositar: ");
        if (monto == null || montoInvalido(monto)) return;

        saldo = saldo.add(monto);
        registrarMovimiento("Depósito", monto);
        System.out.printf("Depósito OK → Nuevo saldo: %s%n", formatearDinero(saldo));
        pausar();
    }

    private void mostrarHistorial() {
        if (historial.isEmpty()) {
            System.out.println("\nAún no hay movimientos.");
        } else {
            lineaDecorativa(70);
            System.out.println("           HISTORIAL DE MOVIMIENTOS           ");
            lineaDecorativa(70);
            historial.forEach(System.out::println);
            lineaDecorativa(70);
        }
        pausar();
    }

    private void salir() {
        System.out.println("\nGracias por usar el cajero. ¡Hasta pronto!");
        salir = true;
    }

    // ────────────────────────────────────────────────
    // Utilidades
    // ────────────────────────────────────────────────
    private BigDecimal leerMonto(String prompt) {
        System.out.print(prompt);
        try {
            BigDecimal monto = consola.nextBigDecimal();
            consola.nextLine();
            return monto.setScale(ESCALA_DINERO, RoundingMode.HALF_UP);
        } catch (InputMismatchException e) {
            consola.nextLine();
            System.out.println("Formato inválido. Use números (ej: 2500.50)");
            return null;
        }
    }

    private boolean montoInvalido(BigDecimal monto) {
        if (monto.compareTo(CERO) <= 0) {
            System.out.println("El monto debe ser mayor a $0.");
            pausar();
            return true;
        }
        return false;
    }

    private void registrarMovimiento(String tipo, BigDecimal monto) {
        historial.add(new Movimiento(tipo, monto, saldo));
    }

    private static String formatearDinero(BigDecimal valor) {
        return String.format("$ %,.2f", valor);
    }

    private void pausar() {
        System.out.print("\nPresione ENTER para continuar...");
        consola.nextLine();
    }
}




