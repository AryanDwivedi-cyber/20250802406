class NumberPrinter extends Thread {
    private final int start;
    NumberPrinter(int start) { this.start = start; }
    public void run() {
        for (int i = start; i < start + 5; i++) {
            System.out.println(getName() + " -> " + i);
        }
    }
}
class Main {
    public static void main(String[] args) throws InterruptedException {
        NumberPrinter first = new NumberPrinter(1);
        NumberPrinter second = new NumberPrinter(6);
        first.start();
        second.start();
        first.join();
        second.join();
        System.out.println("Both threads finished.");
    }
}