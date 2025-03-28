package ex3;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

//  Інтерфейс для відображення результатів
interface DisplayableResult {
    void display(); // Базовий метод відображення
    void saveToFile(String filename); // Додатковий метод збереження у файл
}

// Абстрактна фабрика для створення результатів
abstract class ResultFactory {
    public abstract DisplayableResult createResult(Object... values);
}

// Клас результату обчислення площі
class GeometricAreaResult implements DisplayableResult {
    private String shapeType;
    private double area;
    private List<Double> dimensions;

    public GeometricAreaResult(String shapeType, double area, List<Double> dimensions) {
        this.shapeType = shapeType;
        this.area = area;
        this.dimensions = dimensions;
    }

    @Override
    public void display() {
        System.out.println("Геометрична фігура: " + shapeType);
        System.out.println("Параметри: " + dimensions.stream()
                .map(d -> String.format("%.2f", d))
                .collect(Collectors.joining(", ")));
        System.out.printf("Площа: %.2f%n", area);
    }

    @Override
    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write("Геометрична фігура: " + shapeType + "\n");
            writer.write("Параметри: " + dimensions.stream()
                    .map(d -> String.format("%.2f", d))
                    .collect(Collectors.joining(", ")) + "\n");
            writer.write(String.format("Площа: %.2f%n", area));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Помилка збереження у файл: " + e.getMessage());
        }
    }
}

// Фабрика для створення геометричних результатів
class GeometricAreaFactory extends ResultFactory {
    @Override
    public DisplayableResult createResult(Object... values) {
        String shapeType = (String) values[0];
        double area = (double) values[1];
        List<Double> dimensions = (List<Double>) values[2];
        return new GeometricAreaResult(shapeType, area, dimensions);
    }
}

// Клас для підрахунку бітів
class BinaryOnesResult implements DisplayableResult {
    private int onesCount;
    private double originalValue;

    public BinaryOnesResult(int onesCount, double originalValue) {
        this.onesCount = onesCount;
        this.originalValue = originalValue;
    }

    @Override
    public void display() {
        System.out.println("Кількість одиничних бітів: " + onesCount);
        System.out.printf("Оригінальне значення: %.2f%n", originalValue);
    }

    @Override
    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write("Кількість одиничних бітів: " + onesCount + "\n");
            writer.write(String.format("Оригінальне значення: %.2f%n", originalValue));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Помилка збереження у файл: " + e.getMessage());
        }
    }
}

// Клас головної програми
class GeometricCalculator {
    // Методи обчислення площ
    public static double calculateCircleArea(double radius) {
        return Math.PI * radius * radius;
    }

    public static double calculateEllipseArea(double semiMajorAxis, double semiMinorAxis) {
        return Math.PI * semiMajorAxis * semiMinorAxis;
    }

    // Метод підрахунку одиничних бітів
    public static int countBinaryOnes(double value) {
        return Integer.bitCount((int) value);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<DisplayableResult> results = new ArrayList<>();
        ResultFactory geometricFactory = new GeometricAreaFactory();

        // Введення даних для кола
        System.out.print("Введіть радіус кола: ");
        double circleRadius = scanner.nextDouble();

        // Введення даних для еліпса
        System.out.print("Введіть довгу піввісь еліпса: ");
        double ellipseMajorAxis = scanner.nextDouble();
        System.out.print("Введіть коротку піввісь еліпса: ");
        double ellipseMinorAxis = scanner.nextDouble();

        // Обчислення площ
        double circleArea = calculateCircleArea(circleRadius);
        double ellipseArea = calculateEllipseArea(ellipseMajorAxis, ellipseMinorAxis);
        double totalArea = circleArea + ellipseArea;

        // Створення та додавання результатів
        results.add(geometricFactory.createResult(
                "Коло",
                circleArea,
                Arrays.asList(circleRadius)
        ));

        results.add(geometricFactory.createResult(
                "Еліпс",
                ellipseArea,
                Arrays.asList(ellipseMajorAxis, ellipseMinorAxis)
        ));

        // Додавання результату підрахунку бітів
        results.add(new BinaryOnesResult(
                countBinaryOnes(totalArea),
                totalArea
        ));

        // Виведення результатів
        System.out.println("\nРезультати обчислень:");
        for (DisplayableResult result : results) {
            result.display();
            // Додаткове збереження у файл
            result.saveToFile("avramenko_results_ex3.txt");
        }

        scanner.close();
    }
}