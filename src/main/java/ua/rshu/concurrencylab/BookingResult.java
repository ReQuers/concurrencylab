package ua.rshu.concurrencylab;

public record BookingResult(
        BookingRequest request,
        boolean success,
        String threadInfo,
        boolean virtualThread
) {
}
