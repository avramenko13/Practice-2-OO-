package ex2;


import java.io.*;
        import java.util.Scanner;

public class individual {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Введіть перший кут (у градусах):");
            double angle1 = scanner.nextDouble();

            System.out.println("Введіть другий кут (у градусах):");
            double angle2 = scanner.nextDouble();

            System.out.println("Введіть третій кут (у градусах):");
            double angle3 = scanner.nextDouble();

            System.out.println("Введіть четвертий кут (у градусах):");
            double angle4 = scanner.nextDouble();

            SinAnalyzer data = new SinAnalyzer(angle1, angle2, angle3, angle4);

            // Виведення початкових даних
            System.out.println("\nПочаткові дані:");
            data.printInfo();

            // Серіалізація
            String filename = "C:\\Users\\n1612\\IdeaProjects\\practice2avramenko\\avramenkoООП.txt";
            serializeToFile(data, filename);

            // Десеріалізація
            SinAnalyzer loadedData = deserializeFromFile(filename);

            // Виведення завантажених даних
            System.out.println("\nПісля серіалізації:");
            loadedData.printInfo();
        }
    }

    // Метод серіалізації у файл
    private static void serializeToFile(SinAnalyzer data, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(data);
            System.out.println("Дані серіалізовано у " + filename);
        } catch (IOException e) {
            System.err.println("Помилка серіалізації: " + e.getMessage());
        }
    }

    // Метод десеріалізації з файлу
    private static SinAnalyzer deserializeFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (SinAnalyzer) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Помилка десеріалізації: " + e.getMessage());
            return null;
        }
    }
}

class SinAnalyzer implements Serializable {
    private static final long serialVersionUID = 1L;

    private double angle1;
    private double angle2;
    private double angle3;
    private double angle4;

    public SinAnalyzer(double angle1, double angle2, double angle3, double angle4) {
        this.angle1 = angle1;
        this.angle2 = angle2;
        this.angle3 = angle3;
        this.angle4 = angle4;
    }

    // Метод для підрахунку кількості одиниць у двійковому поданні
    private int countBinaryOnes(int number) {
        return Integer.bitCount(Math.abs(number));
    }

    // Метод для обчислення середнього арифметичного значення функції 1000*sin(α)
    private double calculateAverageSin() {
        double sinValue1 = 1000 * Math.sin(Math.toRadians(angle1));
        double sinValue2 = 1000 * Math.sin(Math.toRadians(angle2));
        double sinValue3 = 1000 * Math.sin(Math.toRadians(angle3));
        double sinValue4 = 1000 * Math.sin(Math.toRadians(angle4));

        return (sinValue1 + sinValue2 + sinValue3 + sinValue4) / 4;
    }

    // Метод для виведення інформації
    public void printInfo() {
        System.out.println("Кути: " + angle1 + ", " + angle2 + ", " + angle3 + ", " + angle4);

        double averageSin = calculateAverageSin();
        int integerPartOfAverage = (int) averageSin;

        System.out.println("Середнє арифметичне 1000*sin(α): " + averageSin);
        System.out.println("Ціла частина середнього: " + integerPartOfAverage);


        String binaryRepresentation = Integer.toBinaryString(Math.abs(integerPartOfAverage));
        System.out.println("Двійкове подання цілої частини: " + binaryRepresentation);

        int binaryOnesCount = countBinaryOnes(integerPartOfAverage);
        System.out.println(">>> КІЛЬКІСТЬ ОДИНИЦЬ У ДВІЙКОВОМУ ПОДАННІ: " + binaryOnesCount + " <<<");
    }
}
