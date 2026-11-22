
import java.util.ArrayList;
import java.util.Scanner;

class Train {

    int trainNumber;
    String trainName;
    String source;
    String destination;
    int availableSeats;
    double fare;

    Train(int trainNumber, String trainName, String source,
          String destination, int availableSeats, double fare) {

        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.availableSeats = availableSeats;
        this.fare = fare;
    }

    void displayTrain() {
        System.out.println("Train Number: " + trainNumber);
        System.out.println("Train Name: " + trainName);
        System.out.println("From: " + source);
        System.out.println("To: " + destination);
        System.out.println("Available Seats: " + availableSeats);
        System.out.println("Fare: Rs. " + fare);
        System.out.println("--------------------------------");
    }
}

class Booking {

    int pnr;
    String passengerName;
    int age;
    Train train;

    Booking(int pnr, String passengerName, int age, Train train) {
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.age = age;
        this.train = train;
    }

    void displayBooking() {
        System.out.println("PNR Number: " + pnr);
        System.out.println("Passenger Name: " + passengerName);
        System.out.println("Age: " + age);
        System.out.println("Train: " + train.trainName);
        System.out.println("Route: " + train.source + " to " + train.destination);
        System.out.println("Fare: Rs. " + train.fare);
        System.out.println("--------------------------------");
    }
}

public class Main {

    static ArrayList<Train> trains = new ArrayList<>();
    static ArrayList<Booking> bookings = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    static int nextPNR = 1001;

    public static void main(String[] args) {

        trains.add(new Train(101, "Deccan Express",
                "Pune", "Mumbai", 100, 250));

        trains.add(new Train(102, "Shatabdi Express",
                "Mumbai", "Pune", 80, 500));

        trains.add(new Train(103, "Rajdhani Express",
                "Mumbai", "Delhi", 120, 1500));

        trains.add(new Train(104, "Intercity Express",
                "Pune", "Nashik", 90, 350));

        int choice;

        do {
            System.out.println("\n========================================");
            System.out.println("       RAILWAY RESERVATION SYSTEM");
            System.out.println("========================================");

            System.out.println("1. View Available Trains");
            System.out.println("2. Search Train");
            System.out.println("3. Book Ticket");
            System.out.println("6. View All Bookings");
            System.out.println("7. Exit");

            System.out.print("\nEnter your choice: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1:
                    viewTrains();
                    break;

                case 2:
                    searchTrain();
                    break;

                case 3:
                    bookTicket();
                    break;

                case 6:
                    viewBookings();
                    break;

                case 7:
                    System.out.println("Thank you for using our system!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 7);
    }

    static void viewTrains() {

        for (Train t : trains) {
            t.displayTrain();
        }
    }

    static void searchTrain() {

        System.out.print("Enter Source: ");
        String source = sc.nextLine();

        System.out.print("Enter Destination: ");
        String destination = sc.nextLine();

        boolean found = false;

        for (Train t : trains) {

            if (t.source.equalsIgnoreCase(source)
                    && t.destination.equalsIgnoreCase(destination)) {

                t.displayTrain();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No trains found!");
        }
    }

    static void bookTicket() {

        System.out.print("Enter Train Number: ");
        int number = Integer.parseInt(sc.nextLine());

        Train selectedTrain = null;

        for (Train t : trains) {
            if (t.trainNumber == number) {
                selectedTrain = t;
                break;
            }
        }

        if (selectedTrain == null) {
            System.out.println("Invalid Train Number!");
            return;
        }

        if (selectedTrain.availableSeats <= 0) {
            System.out.println("No seats available!");
            return;
        }

        System.out.print("Enter Passenger Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Passenger Age: ");
        int age = Integer.parseInt(sc.nextLine());

        if (name.trim().isEmpty() || age <= 0 || age > 120) {
            System.out.println("Invalid passenger details!");
            return;
        }

        Booking b = new Booking(nextPNR, name, age, selectedTrain);

        bookings.add(b);
        selectedTrain.availableSeats--;
        
        System.out.println("\nTicket booked successfully!");
        b.displayBooking();

        nextPNR++;
    }

    static void viewBookings() {

        if (bookings.isEmpty()) {
            System.out.println("No bookings available!");
            return;
        }

        for (Booking b : bookings) {
            b.displayBooking();
        }
    }
}
