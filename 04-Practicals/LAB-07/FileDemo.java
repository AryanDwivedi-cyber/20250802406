 	import java.io.*;

public class FileDemo {
    public static void main(String[] args) {

        try {
            // Writing data to a file
            FileWriter writer = new FileWriter("student.txt");

            writer.write("Student Name: Rahul\n");
            writer.write("Roll No: 101\n");
            writer.write("Marks: 85\n");

            writer.close();

            System.out.println("Data written successfully.");

            // Reading data from the file
            BufferedReader reader = new BufferedReader(
                new FileReader("student.txt")
            );

            String line;

            System.out.println("\nFile Contents:");

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }
}
