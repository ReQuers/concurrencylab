package ua.rshu.concurrencylab;

public class TicketOffice {
    private final Object lock = new Object();
    private int ticketsLeft;

    public TicketOffice(int tickets) {
        this.ticketsLeft = tickets;
    }

    /**
     * Навмисно небезпечна реалізація.
     * Вона потрібна, щоб відтворити race condition.
     */
    public BookingResult bookUnsafe(BookingRequest request) {
        simulatePaymentCheck();

        boolean success = false;
        if (ticketsLeft > 0) {
            // Збільшуємо "вікно" для прояву race condition.
            sleep(25);
            ticketsLeft--;
            success = true;
        }

        return result(request, success);
    }

    /**
     * TODO 1.
     * Реалізуйте потокобезпечне бронювання.
     * Важливо: simulatePaymentCheck() НЕ треба виконувати всередині synchronized-блоку.
     * Синхронізуйте лише критичну секцію: перевірку ticketsLeft та зменшення значення.
     */
    public BookingResult bookSafe(BookingRequest request) {
        simulatePaymentCheck();

        // TODO: замініть цю тимчасову реалізацію на synchronized-блок.
        boolean success = false;
        synchronized (lock) {
            if (ticketsLeft > 0) {
                ticketsLeft--;
                success = true;
            }
        }
        return result(request, success);

    }

    public int getTicketsLeft() {
        synchronized (lock){
            return ticketsLeft;
        }
    }

    private BookingResult result(BookingRequest request, boolean success) {
        Thread current = Thread.currentThread();
        String threadInfo = current.toString();
        if (success) {
            System.out.printf("[OK] %-11s | %-18s | virtual=%s%n",
                    request.customer(), shortThread(threadInfo), current.isVirtual());
        }
        return new BookingResult(request, success, threadInfo, current.isVirtual());
    }

    /**
     * Імітація blocking I/O, наприклад перевірки платежу або запиту до зовнішнього сервісу.
     */
    private void simulatePaymentCheck() {
        sleep(100);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String shortThread(String value) {
        return value.length() > 18 ? value.substring(0, 18) : value;
    }
}
