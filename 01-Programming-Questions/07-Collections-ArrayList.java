import java.util.ArrayList;
import java.util.Collections;
class Main {
    public static void main(String[] args) {
        ArrayList<String> subjects = new ArrayList<>();
        subjects.add("Java");
        subjects.add("DAA");
        subjects.add("DBMS");
        Collections.sort(subjects);
        for (String subject : subjects) System.out.println(subject);
    }
}