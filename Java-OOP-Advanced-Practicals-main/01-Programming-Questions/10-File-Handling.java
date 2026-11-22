import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
class Main {
    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("student.txt"))) {
            writer.write("Roll No: 101");
            writer.newLine();
            writer.write("Name: Aarav");
            System.out.println("File written successfully.");
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }
}