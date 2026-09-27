package ua.rshu.concurrencylab;

public record SimulationResult(
        String scenario,
        int successfulBookings,
        int ticketsLeft,
        long elapsedMs
) {
    public void printSummary() {
        System.out.printf("%n=== %s ===%n", scenario);
        System.out.printf("Успішних бронювань: %d%n", successfulBookings);
        System.out.printf("Квитків залишилось: %d%n", ticketsLeft);
        System.out.printf("Час виконання: %d ms%n", elapsedMs);
    }
}
