package ex6;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class mainex6 {
}



// Інтерфейси
interface Task {
    void execute();
}

interface Displayable {
    void display();
}

// Базові класи для математичних результатів
class ComputationResult implements Displayable {
    protected double value;
    protected String name;

    public ComputationResult(double value, String name) {
        this.value = value;
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    @Override
    public void display() {
        System.out.printf("%s: %.2f\n", name, value);
    }
}

// Геометричні фігури
class Rectangle extends ComputationResult {
    private double width, height;

    public Rectangle(double width, double height) {
        super(2 * (width + height), "Прямокутник");
        this.width = width;
        this.height = height;
    }

    @Override
    public void display() {
        System.out.printf("Прямокутник [ш=%.2f, в=%.2f] -> Периметр: %.2f\n", width, height, value);
    }
}

class Triangle extends ComputationResult {
    private double a, b, c;

    public Triangle(double a, double b, double c) {
        super(a + b + c, "Трикутник");
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public void display() {
        System.out.printf("Трикутник [a=%.2f, b=%.2f, c=%.2f] -> Периметр: %.2f\n", a, b, c, value);
    }
}

// Клас для числових даних
class NumberData implements Displayable {
    private int value;

    public NumberData(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void display() {
        System.out.println("Число: " + value);
    }
}

// Команди для виконання
class GenerateRandomShapesCommand implements Task {
    private List<ComputationResult> shapes;
    private int count;

    public GenerateRandomShapesCommand(List<ComputationResult> shapes, int count) {
        this.shapes = shapes;
        this.count = count;
    }

    @Override
    public void execute() {
        shapes.clear();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            int type = random.nextInt(2); // 0 - прямокутник, 1 - трикутник

            if (type == 0) {
                double width = 1 + random.nextDouble() * 10;
                double height = 1 + random.nextDouble() * 10;
                shapes.add(new Rectangle(width, height));
            } else {
                double a = 1 + random.nextDouble() * 5;
                double b = 1 + random.nextDouble() * 5;
                double c = 1 + random.nextDouble() * 5;
                shapes.add(new Triangle(a, b, c));
            }
        }

        System.out.println("Згенеровано " + count + " фігур:");
        for (Displayable shape : shapes) {
            shape.display();
        }
    }
}

class GenerateRandomNumbersCommand implements Task {
    private List<NumberData> numbers;
    private int count;
    private int maxValue;

    public GenerateRandomNumbersCommand(List<NumberData> numbers, int count, int maxValue) {
        this.numbers = numbers;
        this.count = count;
        this.maxValue = maxValue;
    }

    @Override
    public void execute() {
        numbers.clear();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            numbers.add(new NumberData(random.nextInt(maxValue)));
        }

        System.out.println("Згенеровано " + count + " чисел:");
        for (NumberData number : numbers) {
            number.display();
        }
    }
}

class ProcessShapesCommand implements Task {
    private List<ComputationResult> shapes;

    public ProcessShapesCommand(List<ComputationResult> shapes) {
        this.shapes = shapes;
    }

    @Override
    public void execute() {
        if (shapes.isEmpty()) {
            System.out.println("Немає фігур для обробки.");
            return;
        }

        System.out.println("\nВиконується паралельна обробка фігур...");

        // Використання CompletableFuture для паралельних обчислень
        CompletableFuture<Double> minFuture = CompletableFuture.supplyAsync(() ->
                shapes.parallelStream().mapToDouble(ComputationResult::getValue).min().orElse(0)
        );

        CompletableFuture<Double> maxFuture = CompletableFuture.supplyAsync(() ->
                shapes.parallelStream().mapToDouble(ComputationResult::getValue).max().orElse(0)
        );

        CompletableFuture<Double> avgFuture = CompletableFuture.supplyAsync(() ->
                shapes.parallelStream().mapToDouble(ComputationResult::getValue).average().orElse(0)
        );

        CompletableFuture<List<ComputationResult>> filteredFuture = CompletableFuture.supplyAsync(() ->
                shapes.parallelStream()
                        .filter(shape -> shape.getValue() > 15)
                        .collect(Collectors.toList())
        );

        try {
            CompletableFuture.allOf(minFuture, maxFuture, avgFuture, filteredFuture).get();

            double min = minFuture.get();
            double max = maxFuture.get();
            double avg = avgFuture.get();
            List<ComputationResult> filtered = filteredFuture.get();

            System.out.println("Результати аналізу фігур:");
            System.out.printf("Мінімальний периметр: %.2f\n", min);
            System.out.printf("Максимальний периметр: %.2f\n", max);
            System.out.printf("Середній периметр: %.2f\n", avg);
            System.out.println("Фігури з периметром > 15: " + filtered.size());

            // Статистична обробка
            DoubleSummaryStatistics stats = shapes.parallelStream()
                    .mapToDouble(ComputationResult::getValue)
                    .summaryStatistics();

            System.out.println("\nЗагальна статистика:");
            System.out.println("Кількість фігур: " + stats.getCount());
            System.out.printf("Сума периметрів: %.2f\n", stats.getSum());
            System.out.printf("Мінімум: %.2f\n", stats.getMin());
            System.out.printf("Максимум: %.2f\n", stats.getMax());
            System.out.printf("Середнє: %.2f\n", stats.getAverage());

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Помилка при паралельній обробці: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}

class ProcessNumbersCommand implements Task {
    private List<NumberData> numbers;

    public ProcessNumbersCommand(List<NumberData> numbers) {
        this.numbers = numbers;
    }

    @Override
    public void execute() {
        if (numbers.isEmpty()) {
            System.out.println("Немає чисел для обробки.");
            return;
        }

        System.out.println("\nВиконується паралельна обробка чисел...");

        // Паралельна обробка з використанням parallelStream
        CompletableFuture<Integer> minFuture = CompletableFuture.supplyAsync(() ->
                numbers.parallelStream().mapToInt(NumberData::getValue).min().orElse(0)
        );

        CompletableFuture<Integer> maxFuture = CompletableFuture.supplyAsync(() ->
                numbers.parallelStream().mapToInt(NumberData::getValue).max().orElse(0)
        );

        CompletableFuture<Double> avgFuture = CompletableFuture.supplyAsync(() ->
                numbers.parallelStream().mapToInt(NumberData::getValue).average().orElse(0)
        );

        CompletableFuture<List<NumberData>> evenFuture = CompletableFuture.supplyAsync(() ->
                numbers.parallelStream()
                        .filter(n -> n.getValue() % 2 == 0)
                        .collect(Collectors.toList())
        );

        CompletableFuture<Map<Boolean, List<NumberData>>> partitionFuture = CompletableFuture.supplyAsync(() ->
                numbers.parallelStream()
                        .collect(Collectors.partitioningBy(n -> n.getValue() > 50))
        );

        try {
            CompletableFuture.allOf(minFuture, maxFuture, avgFuture, evenFuture, partitionFuture).get();

            int min = minFuture.get();
            int max = maxFuture.get();
            double avg = avgFuture.get();
            List<NumberData> evenNumbers = evenFuture.get();
            Map<Boolean, List<NumberData>> partitioned = partitionFuture.get();

            System.out.println("Результати аналізу чисел:");
            System.out.println("Мінімальне число: " + min);
            System.out.println("Максимальне число: " + max);
            System.out.printf("Середнє значення: %.2f\n", avg);
            System.out.println("Кількість парних чисел: " + evenNumbers.size());
            System.out.println("Кількість чисел > 50: " + partitioned.get(true).size());
            System.out.println("Кількість чисел <= 50: " + partitioned.get(false).size());

            // Статистична обробка
            IntSummaryStatistics stats = numbers.parallelStream()
                    .mapToInt(NumberData::getValue)
                    .summaryStatistics();

            System.out.println("\nЗагальна статистика:");
            System.out.println("Кількість чисел: " + stats.getCount());
            System.out.println("Сума: " + stats.getSum());
            System.out.println("Мінімум: " + stats.getMin());
            System.out.println("Максимум: " + stats.getMax());
            System.out.printf("Середнє: %.2f\n", stats.getAverage());

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Помилка при паралельній обробці: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}

// Менеджер команд (Worker Thread pattern)
class CommandManager {
    private static CommandManager instance;
    private final BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor;

    private CommandManager() {
        executor = Executors.newSingleThreadExecutor();

        // Запуск робочого потоку для обробки завдань
        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Task task = taskQueue.take(); // Блокуючий метод, очікує на завдання
                    System.out.println("\nВиконання завдання...");
                    task.execute();
                    System.out.println("Завдання виконано");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("\nРоботу перервано");
                    break;
                }
            }
        });
    }

    public static synchronized CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    public void addTask(Task task) {
        try {
            taskQueue.put(task);
            System.out.println("\nЗавдання додано до черги");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("\nПомилка при додаванні завдання: " + e.getMessage());
        }
    }

    public int getQueueSize() {
        return taskQueue.size();
    }

    public void shutdown() {
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Пул потоків не завершив роботу коректно");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Головний клас програми
class DataProcessor {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<ComputationResult> shapes = new ArrayList<>();
        List<NumberData> numbers = new ArrayList<>();
        CommandManager manager = CommandManager.getInstance();

        boolean running = true;

        System.out.println("--- Завдання №6 Авраменко Олександр ---");
        System.out.println("Демонстрація паралельної обробки та шаблону Worker Thread");

        while (running) {
            System.out.println("\n--- Головне меню програми ---");
            System.out.println("(1) - Згенерувати випадкові фігури");
            System.out.println("(2) - Згенерувати випадкові числа");
            System.out.println("(3) - Обробити фігури паралельно");
            System.out.println("(4) - Обробити числа паралельно");
            System.out.println("(5) - Показати поточні дані");
            System.out.println("(6) - Показати кількість завдань у черзі");
            System.out.println("(7) - Вихід");
            System.out.print("Ваш вибір: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // очищення буфера

                // Видалено вивід "Вибрано опцію: " + choice

                switch (choice) {
                    case 1:
                        System.out.print("\nВведіть кількість фігур для генерації: ");
                        int shapeCount = scanner.nextInt();
                        Task generateShapesTask = new GenerateRandomShapesCommand(shapes, shapeCount);
                        manager.addTask(generateShapesTask);
                        break;

                    case 2:
                        System.out.print("\nВведіть кількість чисел для генерації: ");
                        int numCount = scanner.nextInt();
                        System.out.print("Введіть максимальне значення: ");
                        int maxValue = scanner.nextInt();
                        Task generateNumbersTask = new GenerateRandomNumbersCommand(numbers, numCount, maxValue);
                        manager.addTask(generateNumbersTask);
                        break;

                    case 3:
                        if (shapes.isEmpty()) {
                            System.out.println("\nСпочатку згенеруйте фігури!");
                        } else {
                            Task processShapesTask = new ProcessShapesCommand(shapes);
                            manager.addTask(processShapesTask);
                        }
                        break;

                    case 4:
                        if (numbers.isEmpty()) {
                            System.out.println("\nСпочатку згенеруйте числа!");
                        } else {
                            Task processNumbersTask = new ProcessNumbersCommand(numbers);
                            manager.addTask(processNumbersTask);
                        }
                        break;

                    case 5:
                        System.out.println("\n--- Поточні дані ---");
                        if (!shapes.isEmpty()) {
                            System.out.println("Фігури (" + shapes.size() + "):");
                            for (Displayable shape : shapes) {
                                shape.display();
                            }
                        } else {
                            System.out.println("Фігури: немає даних");
                        }

                        if (!numbers.isEmpty()) {
                            System.out.println("\nЧисла (" + numbers.size() + "):");
                            for (Displayable number : numbers) {
                                number.display();
                            }
                        } else {
                            System.out.println("Числа: немає даних");
                        }
                        break;

                    case 6:
                        System.out.println("\nКількість завдань у черзі: " + manager.getQueueSize());
                        break;

                    case 7:
                        running = false;
                        manager.shutdown();
                        System.out.println("\nЗавершення програми.");
                        break;

                    default:
                        System.out.println("\nНевідома команда. Спробуйте ще раз.");
                }

            } catch (InputMismatchException e) {
                System.out.println("\nПомилка вводу. Введіть коректне значення.");
                scanner.nextLine(); // очищення буфера
            }
        }

        scanner.close();
    }
}