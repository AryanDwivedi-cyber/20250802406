import java.util.HashMap;
import java.util.Map;
class Main {
    public static void main(String[] args) {
        Map<Integer, String> students = new HashMap<>();
        students.put(101, "Aarav");
        students.put(102, "Diya");
        students.put(103, "Kabir");
        for (Map.Entry<Integer, String> entry : students.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}