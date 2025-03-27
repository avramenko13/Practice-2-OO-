package ex1;

public class Main {

    public static void main(String[] args) {
        System.out.println("Практика ООП Авраменко");

        if (args.length == 0) {
            System.out.println("Завдання 1");
        } else {
            for (int i = 0; i < args.length; i++) {
                System.out.println("Аргумент " + (i + 1) + ": " + args[i]);
            }
        }
    }
}