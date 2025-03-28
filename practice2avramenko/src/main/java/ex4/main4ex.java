package ex4;

import java.util.*;
import java.util.stream.Collectors;


//Інтерфейс для відображення геометричних результатів.
//Визначає методи виведення даних у різних форматах.

interface GeometricResultDisplay {

    //Базове виведення результатів.

    void displayBasic();

    // Налаштоване виведення результатів у вигляді таблиці.

    void displayCustomTable(int columns, int cellWidth);
}


//Абстрактний клас для зберігання результатів геометричних обчислень.

abstract class GeometricResult implements GeometricResultDisplay {
    protected double calculatedValue;
    protected String shapeType;
    protected List<Double> dimensions;


    //Конструктор для ініціалізації базових параметрів результату.

    public GeometricResult(String shapeType, double calculatedValue, List<Double> dimensions) {
        this.shapeType = shapeType;
        this.calculatedValue = calculatedValue;
        this.dimensions = dimensions;
    }

    @Override
    public void displayBasic() {
        System.out.printf("Фігура: %s%n", shapeType);
        System.out.printf("Результат обчислення: %.2f%n", calculatedValue);
    }

    @Override
    public void displayCustomTable(int columns, int cellWidth) {
        System.out.println("Таблиця з результатами:");
        printTableHeader(columns, cellWidth);

        // Виведення параметрів та результату

        List<String> rowData = new ArrayList<>(dimensions.stream()
                .map(d -> String.format("%.2f", d))
                .collect(Collectors.toList()));
        rowData.add(String.format("%.2f", calculatedValue));

        printTableRow(rowData, columns, cellWidth);
        printTableFooter(columns, cellWidth);
    }


    //Допоміжний метод для виведення заголовка таблиці.

    private void printTableHeader(int columns, int cellWidth) {
        printHorizontalLine(columns, cellWidth);
        System.out.printf("| %-" + (columns * cellWidth - 4) + "s |\n", shapeType);
        printHorizontalLine(columns, cellWidth);
    }


    //Допоміжний метод для виведення рядка таблиці.

    private void printTableRow(List<String> data, int columns, int cellWidth) {
        for (int i = 0; i < data.size(); i++) {
            System.out.printf("| %" + (cellWidth - 2) + "s ", data.get(i));

            if ((i + 1) % columns == 0 || i == data.size() - 1) {
                System.out.println("|");
                printHorizontalLine(columns, cellWidth);
            }
        }
    }


    //Допоміжний метод для виведення footer таблиці.

    private void printTableFooter(int columns, int cellWidth) {
        // Додаткова логіка footer при необхідності
    }


    //Допоміжний метод для виведення горизонтальної лінії.

    private void printHorizontalLine(int columns, int cellWidth) {
        for (int i = 0; i < columns; i++) {
            System.out.print("+");
            for (int j = 0; j < cellWidth; j++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }
}


//Клас результату обчислення площі кола.

class CircleAreaResult extends GeometricResult {

    //Конструктор для створення результату обчислення площі кола.

    public CircleAreaResult(double radius, double area) {
        super("коло", area, Arrays.asList(radius));
    }


    //Перевизначений метод для додаткового виведення специфічної інформації.

    @Override
    public void displayBasic() {
        super.displayBasic();
        System.out.printf("Радіус: %.2f%n", dimensions.get(0));
    }
}


//Клас результату обчислення площі прямокутника.

class RectangleAreaResult extends GeometricResult {

    //Конструктор для створення результату обчислення площі прямокутника.

    public RectangleAreaResult(double width, double height, double area) {
        super("прямокутник", area, Arrays.asList(width, height));
    }

    //Перевизначений метод для додаткового виведення специфічної інформації.

    @Override
    public void displayBasic() {
        super.displayBasic();
        System.out.printf("Ширина: %.2f%n", dimensions.get(0));
        System.out.printf("Висота: %.2f%n", dimensions.get(1));
    }
}


//Абстрактна фабрика для створення геометричних результатів.

abstract class GeometricResultFactory {

    //Абстрактний метод для створення результату.

    public abstract GeometricResult createResult(Object... params);
}


//Фабрика для створення результатів площі кола.

class CircleAreaFactory extends GeometricResultFactory {
    @Override
    public GeometricResult createResult(Object... params) {
        double radius = (double) params[0];
        double area = Math.PI * radius * radius;
        return new CircleAreaResult(radius, area);
    }
}


//Фабрика для створення результатів площі прямокутника.

class RectangleAreaFactory extends GeometricResultFactory {
    @Override
    public GeometricResult createResult(Object... params) {
        double width = (double) params[0];
        double height = (double) params[1];
        double area = width * height;
        return new RectangleAreaResult(width, height, area);
    }
}


//Клас для тестування функціональності калькулятора.

class GeometricCalculatorTest {

    //Тестування створення та виведення результатів.

    public boolean testResultCreationAndDisplay() {
        GeometricResultFactory circleFactory = new CircleAreaFactory();
        GeometricResultFactory rectangleFactory = new RectangleAreaFactory();

        try {
            GeometricResult circleResult = circleFactory.createResult(5.0);
            GeometricResult rectangleResult = rectangleFactory.createResult(4.0, 6.0);

            circleResult.displayBasic();
            rectangleResult.displayBasic();

            circleResult.displayCustomTable(2, 10);
            rectangleResult.displayCustomTable(3, 8);

            return true;
        } catch (Exception e) {
            System.err.println("Помилка в тестуванні: " + e.getMessage());
            return false;
        }
    }


    //Запуск тестів.

    public void runTests() {
        System.out.println("Виконання тестів");
        boolean testResult = testResultCreationAndDisplay();
        System.out.println("Тест " + (testResult ? "успішно пройдено" : "провалено"));
    }
}


//Головний клас програми.

class GeometricCalculator {

    //Головний метод для запуску програми.

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GeometricCalculatorTest test = new GeometricCalculatorTest();
        List<GeometricResult> results = new ArrayList<>();

        boolean running = true;
        while (running) {
            System.out.println("\nГоловне меню ");
            System.out.println("(1) Обчислення площі кола ");
            System.out.println("(2) Обчислення площі прямокутника ");
            System.out.println("(3) Результат ");
            System.out.println("(4) Тестування ");
            System.out.println("(5) Завершити");
            System.out.print(" Виберіть дію: ");

            try {
                int choice = scanner.nextInt();
                GeometricResultFactory factory;

                switch (choice) {
                    case 1:
                        System.out.print("Введіть радіус кола: ");
                        double radius = scanner.nextDouble();
                        factory = new CircleAreaFactory();
                        results.add(factory.createResult(radius));
                        break;
                    case 2:
                        System.out.print("Введіть ширину прямокутника: ");
                        double width = scanner.nextDouble();
                        System.out.print("Введіть висоту прямокутника: ");
                        double height = scanner.nextDouble();
                        factory = new RectangleAreaFactory();
                        results.add(factory.createResult(width, height));
                        break;
                    case 3:
                        if (results.isEmpty()) {
                            System.out.println("Немає збережених результатів");
                            continue;
                        }
                        System.out.println("Оберіть формат виведення: (1 - текстовий, 2 - таблиця)");
                        int displayChoice = scanner.nextInt();

                        if (displayChoice == 1) {
                            results.forEach(GeometricResult::displayBasic);
                        } else if (displayChoice == 2) {
                            System.out.print("Введіть кількість стовпців: ");
                            int columns = scanner.nextInt();
                            System.out.print("Введіть ширину комірки: ");
                            int cellWidth = scanner.nextInt();
                            results.forEach(r -> r.displayCustomTable(columns, cellWidth));
                        }
                        break;
                    case 4:
                        test.runTests();
                        break;
                    case 5:
                        running = false;
                        break;
                    default:
                        System.out.println("Невідома команда");
                }
            } catch (InputMismatchException e) {
                System.out.println("Помилка вводу. Введіть коректне значення.");
                scanner.nextLine(); // Очищення буфера
            }
        }
        scanner.close();
    }
}