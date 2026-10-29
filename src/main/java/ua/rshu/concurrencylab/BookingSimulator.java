package ua.rshu.concurrencylab;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class BookingSimulator {

    public SimulationResult runSequential(List<BookingRequest> requests, int tickets) {
        TicketOffice office = new TicketOffice(tickets);
        long started = System.nanoTime();

        List<BookingResult> results = new ArrayList<>();
        for (BookingRequest request : requests) {
            results.add(office.bookUnsafe(request));
        }

        return result("1. Послідовна обробка", results, office, started);
    }

    public SimulationResult runFixedPoolUnsafe(List<BookingRequest> requests, int tickets, int poolSize) {
        TicketOffice office = new TicketOffice(tickets);
        long started = System.nanoTime();
        return runConcurrent(
                "2. Fixed pool UNSAFE (" + poolSize + " потоків)",
                requests, office, started,
                Executors.newFixedThreadPool(poolSize),
                office::bookUnsafe
        );
    }

    public SimulationResult runFixedPoolSafe(List<BookingRequest> requests, int tickets, int poolSize) {
        TicketOffice office = new TicketOffice(tickets);
        long started = System.nanoTime();

        return runConcurrent(
                "3. Fixed pool SAFE (" + poolSize + " потоків)",
                requests, office, started,
                Executors.newFixedThreadPool(poolSize),
                office::bookSafe
        );
    }

    public SimulationResult runVirtualThreads(List<BookingRequest> requests, int tickets) {
        TicketOffice office = new TicketOffice(tickets);
        long started = System.nanoTime();

        return runConcurrent(
          "4.Virtual threads (safe)",
                requests,office,started,
                Executors.newVirtualThreadPerTaskExecutor(),
                office::bookSafe
        );
    }

    /**
     * Допоміжний метод уже готовий. Він приймає executor і функцію бронювання.
     * Ваше завдання полягає не в написанні службового коду, а у виборі правильної моделі виконання.
     */
    private SimulationResult runConcurrent(
            String scenario,
            List<BookingRequest> requests,
            TicketOffice office,
            long started,
            ExecutorService executor,
            Function<BookingRequest, BookingResult> bookingFunction
    ) {
        try (executor) {
            List<Future<BookingResult>> futures = new ArrayList<>();

            for (BookingRequest request : requests) {
                futures.add(executor.submit(() -> bookingFunction.apply(request)));
            }

            List<BookingResult> results = new ArrayList<>();
            for (Future<BookingResult> future : futures) {
                try {
                    results.add(future.get());
                } catch (Exception e) {
                    throw new RuntimeException("Помилка виконання задачі", e);
                }
            }

            return result(scenario, results, office, started);
        }
    }

    private SimulationResult result(
            String scenario,
            List<BookingResult> results,
            TicketOffice office,
            long started
    ) {
        long elapsedMs = (System.nanoTime() - started) / 1_000_000;
        int successful = (int) results.stream().filter(BookingResult::success).count();
        return new SimulationResult(scenario, successful, office.getTicketsLeft(), elapsedMs);
    }
}
