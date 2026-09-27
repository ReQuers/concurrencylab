# Лабораторна робота: Ticket Booking Service

## Сценарій
Є 10 квитків на подію та 30 одночасних запитів на бронювання. Перевірка платежу займає приблизно 100 мс і моделює blocking I/O.

Ваше завдання: пройти шлях від послідовного виконання до ExecutorService, відтворити race condition, виправити її через synchronized та на завершення спробувати Virtual Threads.

## Вимоги
- JDK 21
- Maven 3.8+

## Запуск

```bash
mvn -q compile exec:java -Dexec.args="sequential"
```

Після виконання TODO будуть доступні режими:

```bash
mvn -q compile exec:java -Dexec.args="unsafe"
mvn -q compile exec:java -Dexec.args="safe"
mvn -q compile exec:java -Dexec.args="virtual"
```

## TODO
1. `TicketOffice.bookSafe()` — синхронізувати лише критичну секцію.
2. `BookingSimulator.runFixedPoolUnsafe()` — створити fixed thread pool та запустити небезпечну версію.
3. `BookingSimulator.runFixedPoolSafe()` — повторити з потокобезпечною версією.
4. `BookingSimulator.runVirtualThreads()` — використати virtual-thread-per-task executor.

## Що спостерігати
- Час виконання кожного сценарію.
- Кількість успішних бронювань.
- Значення `ticketsLeft`.
- Назви/типи потоків у консолі.
- Чому конкурентна небезпечна версія іноді продає більше квитків, ніж існує.

> Це навчальна симуляція. `Thread.sleep()` використовується як модель очікування blocking I/O, а не як точний performance benchmark.
