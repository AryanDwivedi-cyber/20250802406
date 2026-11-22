class MathOperations {
    public int add(int a, int b) {
        return a + b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public int square(int a) {
        return a * a;
    }
}

public class PackageDemo {
    public static void main(String[] args) {
        MathOperations m = new MathOperations();

        System.out.println("Addition of 15 and 25 = " + m.add(15, 25));
        System.out.println("Multiplication of 6 and 7 = " + m.multiply(6, 7));
        System.out.println("Square of 9 = " + m.square(9));
    }
}
