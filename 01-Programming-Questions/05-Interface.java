interface Payable {
    double calculatePayment();
}
class Employee implements Payable {
    private final double salary;
    Employee(double salary) { this.salary = salary; }
    public double calculatePayment() { return salary; }
}
class Main {
    public static void main(String[] args) {
        Payable p = new Employee(35000);
        System.out.println("Payment: " + p.calculatePayment());
    }
}