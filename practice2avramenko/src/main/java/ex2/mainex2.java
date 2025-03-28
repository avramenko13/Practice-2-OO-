public class mainex2 {

}

// створення унікальних аргументів
public static void main (String[] args) {
    Scanner sc = new Scanner(System.in);
    Set<String> argumentsSet = new LinkedHashSet<>();

    System.out.println("Введіть аргументи (або залиште порожній рядок для завершення):");

    while (true) {
        String input = sc.nextLine().trim();
        if (input.isEmpty()) {
            break;
        }
        if (argumentsSet.contains(input)) {
            System.out.println("Аргумент вже введений: " + input);
        } else {
            argumentsSet.add(input);
        }
    }

    System.out.println("\nпочаткові дані:");
    if (argumentsSet.isEmpty()) {
        System.out.println("Аргументів немає.");
    } else {
        int index = 1;
        for (String arg : argumentsSet) {
            System.out.println("Аргумент " + index + ": " + arg);
            index++;
        }
    }

    // збереження аргументів у файл

    String fileName = "arguments.avramenko";
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
        oos.writeObject(new ArrayList<>(argumentsSet));
        System.out.println("\nРезультат збережено у " + fileName);
    } catch (IOException e) {
        e.printStackTrace();
    }

    // читання аргументів із файлу

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
        List<String> restoredList = (List<String>) ois.readObject();
        System.out.println("\nпісля серіалізації:");
        for (int i = 0; i < restoredList.size(); i++) {
            System.out.println("Аргумент " + (i + 1) + ": " + restoredList.get(i));
        }
    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }
}