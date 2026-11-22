abstract class Shape {
    abstract double area();
    void describe() { System.out.println("This is a shape."); }
}
class Circle extends Shape {
    private final double radius;
    Circle(double radius) { this.radius = radius; }
    @Override
    double area() { return Math.PI * radius * radius; }
}
class Main {
    public static void main(String[] args) {
        Shape s = new Circle(3);
        s.describe();
        System.out.printf("Area: %.2f%n", s.area());
    }
}