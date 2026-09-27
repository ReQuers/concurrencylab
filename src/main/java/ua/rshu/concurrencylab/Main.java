package ua.rshu.concurrencylab;

import java.util.List;
import java.util.stream.IntStream;

public class Main {
    private static final int TICKETS = 10;
    private static final int REQUESTS = 30;
    private static final int POOL_SIZE = 12;

    public static void main(String[] args) {
        String mode = args.length == 0 ? "sequential" : args[0].toLowerCase();
        List<BookingRequest> requests = createRequests();
        BookingSimulator simulator = new BookingSimulator();

        SimulationResult result = switch (mode) {
            case "sequential" -> simulator.runSequential(requests, TICKETS);
            case "unsafe" -> simulator.runFixedPoolUnsafe(requests, TICKETS, POOL_SIZE);
            case "safe" -> simulator.runFixedPoolSafe(requests, TICKETS, POOL_SIZE);
            case "virtual" -> simulator.runVirtualThreads(requests, TICKETS);
            default -> throw new IllegalArgumentException(
                    "Режими: sequential | unsafe | safe | virtual");
        };

        result.printSummary();
    }

    private static List<BookingRequest> createRequests() {
        return IntStream.rangeClosed(1, REQUESTS)
                .mapToObj(i -> new BookingRequest(i, "User-%02d".formatted(i)))
                .toList();
    }
}
