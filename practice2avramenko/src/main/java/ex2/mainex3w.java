package ex2;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class mainex3w {
    public mainex3w(List<String> arguments) {
    }

    public static void main(String[] args) {
        try (Scanner inputCollector = new Scanner(System.in)) {
            System.out.println("Введіть перше значення:");
            int valueA = inputCollector.nextInt();

            System.out.println("Введіть друге значення:");
            int valueB = inputCollector.nextInt();

            System.out.println("Введіть трете значення:");
            int valueC = inputCollector.nextInt();

            NumericContainer dataSet = new NumericContainer(valueA, valueB, valueC);

            // Відображення initial інформації
            System.out.println("\nПочаткові параметри:");
            dataSet.displayNumericDetails();

            // Серіалізація
            String filename = "C:\\Users\\n1612\\IdeaProjects\\practice2avramenko\\avramenkoООП.txt";
            performSerialization(dataSet, filename);

            // Десеріалізація
            NumericContainer restoredData = retrieveSerializedData(filename);

            // Виведення відновлених даних
            System.out.println("\nПісля відновлення:");
            restoredData.displayNumericDetails();
        }
    }

    // Метод серіалізації у файл
    private static void performSerialization(NumericContainer data, String filename) {
        try (ObjectOutputStream serializer = new ObjectOutputStream(new FileOutputStream(filename))) {
            serializer.writeObject(data);
            System.out.println("Дані збережено у " + filename);
        } catch (IOException e) {
            System.err.println("Помилка збереження: " + e.getMessage());
        }
    }

    // Метод десеріалізації з файлу
    private static NumericContainer retrieveSerializedData(String filename) {
        try (ObjectInputStream deserializer = new ObjectInputStream(new FileInputStream(filename))) {
            return (NumericContainer) deserializer.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Помилка відновлення: " + e.getMessage());
            return null;
        }
    }

    public NumericInfo getProcessingData() {
        NumericInfo o = null;
        return o;
    }

    public void archiveData(String filename) {
    }

    public void restoreData(String filename) {
    }
}

class NumericContainer implements Serializable {
    private static final long serialVersionUID = 2L;

    private int a;
    private int b;
    private int c;

    public NumericContainer(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    // Розширений метод для виведення інформації про числа
    public void displayNumericDetails() {
        System.out.println("Перше значення: " + a);
        System.out.println("Друге значення: " + b);
        System.out.println("Третє значення: " + c);
        System.out.println("Сумарне значення: " + calculateSum());
        System.out.println("Добуток значень: " + calculateProduct());
        System.out.println("Середнє арифметичне: " + calculateAverage());
    }

    private int calculateSum() {
        return a + b + c;
    }

    private int calculateProduct() {
        return a * b * c;
    }

    private double calculateAverage() {
        return (double) calculateSum() / 3;
    }
}