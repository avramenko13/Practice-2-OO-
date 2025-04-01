package ex5;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class mainex5w {
}


//Інтерфейс для команд з можливістю скасування.

interface IOperation {
    void execute();
    void undo();
}


//Інтерфейс для відображення результатів.

interface Displayable {
    void display();
}


//Клас для базових результатів.

class Result implements Displayable {
    protected double value;

    public Result(double value) {
        this.value = value;
    }

    @Override
    public void display() {
        System.out.printf("Результат: %.2f\n", value);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}


//Клас для трикутника.

class Triangle extends Result {
    private double a, b, c;

    public Triangle(double a, double b, double c) {
        super(a + b + c); // Периметр
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public void display() {
        System.out.printf("Трикутник [%.1f, %.1f, %.1f] | П: %.2f\n", a, b, c, value);
    }
}


//Клас для прямокутника.

class Rectangle extends Result {
    private double width, height;

    public Rectangle(double width, double height) {
        super(2 * (width + height)); // Периметр
        this.width = width;
        this.height = height;
    }

    @Override
    public void display() {
        System.out.printf("Прямокутник [%.1f × %.1f] | П: %.2f\n", width, height, value);
    }
}


//Інтерфейс для табличного відображення.

interface TableDisplay {
    void showTable(List<Integer> data, int columns);
}


//Клас для відображення в консолі.

class ConsoleDisplay implements TableDisplay {
    @Override
    public void showTable(List<Integer> data, int columns) {
        System.out.println("\nТаблиця:");

        // Верхня межа
        printRowBorder(columns);

        // Дані таблиці
        int itemCount = 0;
        for (int i = 0; i < data.size(); i++) {
            if (itemCount == 0) {
                System.out.print("| ");
            }

            System.out.printf("%3d | ", data.get(i));
            itemCount++;

            if (itemCount == columns || i == data.size() - 1) {
                System.out.println();
                printRowBorder(columns);
                itemCount = 0;
            }
        }
    }

    private void printRowBorder(int columns) {
        for (int i = 0; i < columns; i++) {
            System.out.print("+----");
        }
        System.out.println("+");
    }
}


//Менеджер команд (Singleton).

class CommandManager {
    private static CommandManager instance;
    private Stack<IOperation> history = new Stack<>();

    private CommandManager() {}

    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    public void executeCommand(IOperation command) {
        command.execute();
        history.push(command);
    }

    public void undoLastCommand() {
        if (!history.isEmpty()) {
            IOperation command = history.pop();
            command.undo();
            System.out.println("Останню команду скасовано");
        } else {
            System.out.println("Немає команд для скасування");
        }
    }
}


//Команда масштабування.

class ScaleCommand implements IOperation {
    private List<Displayable> results;
    private double factor;
    private List<Double> previousValues = new ArrayList<>();

    public ScaleCommand(List<Displayable> results, double factor) {
        this.results = results;
        this.factor = factor;
    }

    @Override
    public void execute() {
        if (results.isEmpty()) {
            System.out.println("Немає фігур для масштабування");
            return;
        }

        previousValues.clear();
        for (Displayable item : results) {
            if (item instanceof Result) {
                previousValues.add(((Result) item).getValue());
                ((Result) item).setValue(((Result) item).getValue() * factor);
            }
        }
        System.out.printf("Фігури масштабовано (×%.2f)\n", factor);
    }

    @Override
    public void undo() {
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i) instanceof Result) {
                ((Result) results.get(i)).setValue(previousValues.get(i));
            }
        }
    }
}


//Команда сортування.

class SortCommand implements IOperation {
    private List<Displayable> results;
    private List<Displayable> previousState;

    public SortCommand(List<Displayable> results) {
        this.results = results;
    }

    @Override
    public void execute() {
        if (results.isEmpty()) {
            System.out.println("Немає фігур для сортування");
            return;
        }

        previousState = new ArrayList<>(results);
        results.sort(Comparator.comparingDouble(r -> ((Result) r).getValue()));
        System.out.println("Фігури відсортовано за периметром");
    }

    @Override
    public void undo() {
        results.clear();
        results.addAll(previousState);
    }
}


//Команда обчислення статистики чисел.

class CalculateStatsCommand implements IOperation {
    private List<Integer> numbers;
    private List<Integer> previousNumbers;
    private Map<String, Integer> stats = new HashMap<>();

    public CalculateStatsCommand(List<Integer> numbers) {
        this.numbers = numbers;
        this.previousNumbers = new ArrayList<>(numbers);
    }

    @Override
    public void execute() {
        if (numbers.isEmpty()) {
            System.out.println("Спочатку введіть числа!");
            return;
        }

        // Обчислення статистики
        int sum = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int num : numbers) {
            sum += num;
            if (num < min) min = num;
            if (num > max) max = num;
        }

        stats.put("sum", sum);
        stats.put("min", min);
        stats.put("max", max);
        stats.put("avg", sum / numbers.size());

        System.out.println("\nСтатистика чисел:");
        System.out.println("Сума: " + stats.get("sum"));
        System.out.println("Мінімум: " + stats.get("min"));
        System.out.println("Максимум: " + stats.get("max"));
        System.out.println("Середнє: " + stats.get("avg"));
    }

    @Override
    public void undo() {
        stats.clear();
    }
}


//Команда введення користувацьких чисел.

class InputNumbersCommand implements IOperation {
    private List<Integer> numbers;
    private List<Integer> previousNumbers;
    private Scanner scanner;

    public InputNumbersCommand(List<Integer> numbers, Scanner scanner) {
        this.numbers = numbers;
        this.scanner = scanner;
        this.previousNumbers = new ArrayList<>(numbers);
    }

    @Override
    public void execute() {
        numbers.clear();
        System.out.print("Введіть кількість чисел: ");
        int count = scanner.nextInt();

        System.out.println("Введіть " + count + " чисел (по одному на рядок):");
        for (int i = 0; i < count; i++) {
            System.out.print((i+1) + ": ");
            numbers.add(scanner.nextInt());
        }

        System.out.println("Числа введено: " + numbers);
    }

    @Override
    public void undo() {
        numbers.clear();
        numbers.addAll(previousNumbers);
    }
}


//Макрокоманда - послідовність команд.

class MacroCommand implements IOperation {
    private List<IOperation> commands;
    private List<Integer> dataToSave;

    public MacroCommand(List<IOperation> commands, List<Integer> dataToSave) {
        this.commands = commands;
        this.dataToSave = dataToSave;
    }

    @Override
    public void execute() {
        System.out.println("Виконання макрокоманди...");
        for (IOperation command : commands) {
            command.execute();
        }
        saveToFile(dataToSave);
    }

    @Override
    public void undo() {
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }

    private void saveToFile(List<Integer> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("результати.avramenkoex5.txt"))) {
            for (Integer num : data) {
                writer.write(num + " ");
            }
            writer.newLine();
            System.out.println("Дані збережено в файл 'результати.avramenkoex5.txt'");
        } catch (IOException e) {
            System.err.println("Помилка збереження у файл: " + e.getMessage());
        }
    }
}


//Клас для тестування функціональності.

class FunctionalityTest {
    public static boolean testShapeOperations(List<Displayable> shapes) {
        try {
            System.out.println("\n--- Тестування операцій з фігурами ---");

            // Вивід початкових даних
            System.out.println("Початкові дані:");
            for (Displayable shape : shapes) {
                shape.display();
            }

            // Тест масштабування
            CommandManager manager = CommandManager.getInstance();
            ScaleCommand scaleCmd = new ScaleCommand(shapes, 2.0);
            manager.executeCommand(scaleCmd);

            System.out.println("\nПісля масштабування (×2):");
            for (Displayable shape : shapes) {
                shape.display();
            }

            // Тест скасування операції
            manager.undoLastCommand();
            System.out.println("\nПісля скасування:");
            for (Displayable shape : shapes) {
                shape.display();
            }

            return true;
        } catch (Exception e) {
            System.err.println("Помилка тестування: " + e.getMessage());
            return false;
        }
    }

    public static boolean testTableDisplay(List<Integer> numbers) {
        try {
            System.out.println("\n--- Тестування табличного відображення ---");
            ConsoleDisplay display = new ConsoleDisplay();
            display.showTable(numbers, 3);
            return true;
        } catch (Exception e) {
            System.err.println("Помилка тестування таблиці: " + e.getMessage());
            return false;
        }
    }
}


//Головний клас програми.

class mainex5 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Displayable> shapes = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();
        CommandManager manager = CommandManager.getInstance();
        boolean running = true;

        System.out.println("-- Завдання №5 - Авраменко Олександр --");

        while (running) {
            printMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Очищення буфера

                switch (choice) {
                    case 1:
                        addTriangle(scanner, shapes);
                        break;
                    case 2:
                        addRectangle(scanner, shapes);
                        break;
                    case 3:
                        manager.executeCommand(new ScaleCommand(shapes, getScaleFactor(scanner)));
                        break;
                    case 4:
                        manager.executeCommand(new SortCommand(shapes));
                        break;
                    case 5:
                        displayShapes(shapes);
                        break;
                    case 6:
                        manager.executeCommand(new InputNumbersCommand(numbers, scanner));
                        break;
                    case 7:
                        manager.executeCommand(new CalculateStatsCommand(numbers));
                        break;
                    case 8:
                        displayNumbersTable(numbers);
                        break;
                    case 9:
                        manager.undoLastCommand();
                        break;
                    case 10:
                        executeMacroCommand(manager, shapes, numbers);
                        break;
                    case 11:
                        runTests(shapes, numbers);
                        break;
                    case 0:
                        running = false;
                        System.out.println("Програму завершено.");
                        break;
                    default:
                        System.out.println("Невірний вибір!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Помилка вводу, введіть число");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- ГОЛОВНЕ МЕНЮ ---");
        System.out.println("(1) Додати трикутник");
        System.out.println("(2) Додати прямокутник");
        System.out.println("(3) Масштабувати фігури");
        System.out.println("(4) Сортувати фігури");
        System.out.println("(5) Показати фігури");
        System.out.println("____________________________");
        System.out.println("(6) Ввести числа");
        System.out.println("(7) Статистика чисел");
        System.out.println("(8) Показати таблицю чисел");
        System.out.println("(9) Скасувати останню дію");
        System.out.println("(10) Виконати макрокоманду");
        System.out.println("(11) Запустити тести");
        System.out.println("(0) Вийти");
        System.out.print("Ваш вибір: ");
    }

    private static void addTriangle(Scanner scanner, List<Displayable> shapes) {
        System.out.print("Введіть сторони трикутника через пробіл (a b c): ");
        double a = scanner.nextDouble();
        double b = scanner.nextDouble();
        double c = scanner.nextDouble();
        shapes.add(new Triangle(a, b, c));
        System.out.println("Трикутник додано");
    }

    private static void addRectangle(Scanner scanner, List<Displayable> shapes) {
        System.out.print("Введіть ширину та висоту прямокутника через пробіл (ширина висота): ");
        double width = scanner.nextDouble();
        double height = scanner.nextDouble();
        shapes.add(new Rectangle(width, height));
        System.out.println("Прямокутник додано");
    }

    private static double getScaleFactor(Scanner scanner) {
        System.out.print("Введіть коефіцієнт масштабування: ");
        return scanner.nextDouble();
    }

    private static void displayShapes(List<Displayable> shapes) {
        if (shapes.isEmpty()) {
            System.out.println("Список фігур порожній");
            return;
        }

        System.out.println("\n--- СПИСОК ФІГУР ---");
        for (int i = 0; i < shapes.size(); i++) {
            System.out.print((i+1) + ". ");
            shapes.get(i).display();
        }
    }

    private static void displayNumbersTable(List<Integer> numbers) {
        if (numbers.isEmpty()) {
            System.out.println("Список чисел порожній,спочатку введіть числа.");
            return;
        }

        ConsoleDisplay display = new ConsoleDisplay();
        display.showTable(numbers, 3);
    }

    private static void executeMacroCommand(CommandManager manager, List<Displayable> shapes, List<Integer> numbers) {
        if (numbers.isEmpty()) {
            System.out.println("Спочатку введіть числа для макрокоманди.");
            return;
        }

        List<IOperation> commands = new ArrayList<>();
        // Додаємо команди, які будуть виконані в рамках макрокоманди
        commands.add(new SortCommand(shapes));
        commands.add(new ScaleCommand(shapes, 1.5));
        commands.add(new CalculateStatsCommand(numbers));

        // Створюємо і виконуємо макрокоманду
        MacroCommand macroCmd = new MacroCommand(commands, numbers);
        manager.executeCommand(macroCmd);
    }

    private static void runTests(List<Displayable> shapes, List<Integer> numbers) {
        System.out.println("\n--- ЗАПУСК ТЕСТУВАННЯ ---");

        // Якщо фігури відсутні, додаємо тестові дані
        if (shapes.isEmpty()) {
            shapes.add(new Triangle(3, 4, 5));
            shapes.add(new Rectangle(5, 10));
            System.out.println("Додано тестові фігури для перевірки");
        }

        // Якщо числа відсутні, додаємо тестові дані
        if (numbers.isEmpty()) {
            numbers.addAll(Arrays.asList(5, 10, 15, 20, 25, 30, 35, 40, 45));
            System.out.println("Додано тестові числа для перевірки");
        }

        // Виконуємо тести
        boolean shapesTestResult = FunctionalityTest.testShapeOperations(shapes);
        boolean tableTestResult = FunctionalityTest.testTableDisplay(numbers);

        System.out.println("\nРезультати тестування:");
        System.out.println("Тест операцій з фігурами: " + (shapesTestResult ? "Успішно пройдено" : "Помилка"));
        System.out.println("Тест табличного відображення: " + (tableTestResult ? "Успішно пройдено" : "Помилка"));
    }
}