class Person {
    String name;
    Person(String name) { this.name = name; }
    void display() { System.out.println("Name: " + name); }
}
class Student extends Person {
    int rollNo;
    Student(String name, int rollNo) {
        super(name);
        this.rollNo = rollNo;
    }
    @Override
    void display() {
        super.display();
        System.out.println("Roll number: " + rollNo);
    }
}
class Main {
    public static void main(String[] args) {
        Student s = new Student("Aarav", 101);
        s.display();
    }
}