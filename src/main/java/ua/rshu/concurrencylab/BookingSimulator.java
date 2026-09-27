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

        // TODO 2.
        // Створіть ExecutorService з фіксованим пулом потоків poolSize
        // і передайте його в runConcurrent(...), використовуючи office::bookUnsafe.
        throw new UnsupportedOperationException("TODO 2: реалізуйте fixed thread pool");
    }

    public SimulationResult runFixedPoolSafe(List<BookingRequest> requests, int tickets, int poolSize) {
        TicketOffice office = new TicketOffice(tickets);
        long started = System.nanoTime();

        // TODO 3.
        // Повторіть попередній сценарій, але використайте office::bookSafe.
        throw new UnsupportedOperationException("TODO 3: використайте потокобезпечне бронювання");
    }

    public SimulationResult runVirtualThreads(List<BookingRequest> requests, int tickets) {
        TicketOffice office = new TicketOffice(tickets);
        long started = System.nanoTime();

        // TODO 4.
        // Створіть Executors.newVirtualThreadPerTaskExecutor()
        // і передайте executor у runConcurrent(...), використовуючи office::bookSafe.
        throw new UnsupportedOperationException("TODO 4: реалізуйте virtual threads");
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
