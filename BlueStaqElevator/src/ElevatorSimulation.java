import java.util.*;

// Direction enum
enum Direction {
    UP, DOWN, IDLE
}

// Request class representing a complete trip (pickup + dropoff)
class Request {
    private final int pickupFloor;
    private final int dropoffFloor;
    private final Direction direction;

    public Request(int pickupFloor, int dropoffFloor) {
        this.pickupFloor = pickupFloor;
        this.dropoffFloor = dropoffFloor;
        this.direction = dropoffFloor > pickupFloor ? Direction.UP : Direction.DOWN;
    }

    public int getPickupFloor() { return pickupFloor; }
    public int getDropoffFloor() { return dropoffFloor; }
    public Direction getDirection() { return direction; }
}

// Elevator class
class Elevator {
    private int currentFloor;
    private Direction currentDirection;
    private List<Integer> destinationFloors;
    private final int id;

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 1;
        this.currentDirection = Direction.IDLE;
        this.destinationFloors = new ArrayList<>();
    }

    public void addRequest(Request request) {
        destinationFloors.add(request.getPickupFloor());
        destinationFloors.add(request.getDropoffFloor());
    }

    public String[] move() {
        if (destinationFloors.isEmpty()) {
            currentDirection = Direction.IDLE;
            return null;
        }

        int nextFloor = destinationFloors.getFirst();
        String moves = "";
        String arrivals = "";

        if (nextFloor > currentFloor) {
            currentDirection = Direction.UP;
            currentFloor++;
            moves += "Elevator " + id + ": Moving UP to floor " + currentFloor;
        } else if (nextFloor < currentFloor) {
            currentDirection = Direction.DOWN;
            currentFloor--;
            moves += "Elevator " + id + ": Moving DOWN to floor " + currentFloor;
        }


        if (currentFloor == nextFloor) {
            arrivals += arriveAtFloor();
        }

        return new String[]{moves, arrivals};
    }

    private String arriveAtFloor() {
        destinationFloors.removeFirst();
        return "Elevator " + id + ": Arrived at floor " + currentFloor;
    }

    public boolean canTakeRequest(Request request) {
        // Elevator can take request if:
        // 1. It's idle
        // 2. It's going in the same direction and hasn't passed the pickup floor
        if (currentDirection == Direction.IDLE) {
            return true;
        }

        if (currentDirection == request.getDirection()) {
            if (currentDirection == Direction.UP && request.getPickupFloor() > currentFloor) {
                return true;
            }
            if (currentDirection == Direction.DOWN && request.getPickupFloor() < currentFloor) {
                return true;
            }
        }

        return false;
    }

    // Getters
    public int getCurrentFloor() { return currentFloor; }
    public Direction getCurrentDirection() { return currentDirection; }
    public int getId() { return id; }
    public List<Integer> getDestinationFloors() { return new ArrayList<>(destinationFloors); }
    public int getNumDestinations() { return destinationFloors.size(); }
}

// ElevatorController class
class ElevatorController {
    private final List<Elevator> elevators;
    private final Queue<Request> pendingRequests;
    private final int numFloors;

    public ElevatorController(int numElevators, int numFloors) {
        this.elevators = new ArrayList<>();
        this.pendingRequests = new LinkedList<>();
        this.numFloors = numFloors;

        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i + 1));
        }
    }

    public void requestElevator(int pickupFloor, int dropoffFloor) {
        // Validate floors
        if (pickupFloor < 1 || pickupFloor > numFloors) {
            System.out.println("Invalid pickup floor: " + pickupFloor);
            return;
        }

        if (dropoffFloor < 1 || dropoffFloor > numFloors) {
            System.out.println("Invalid dropoff floor: " + dropoffFloor);
            return;
        }

        if (pickupFloor == dropoffFloor) {
            System.out.println("Pickup and dropoff floors cannot be the same");
            return;
        }

        Request request = new Request(pickupFloor, dropoffFloor);
        String direction = request.getDirection() == Direction.UP ? "UP" : "DOWN";
        System.out.println("Request received: Floor " + pickupFloor + " → " + dropoffFloor + " (" + direction + ")");

        Elevator bestElevator = findBestElevator(request);

        if (bestElevator != null) {
            bestElevator.addRequest(request);
            System.out.println("Assigned to Elevator " + bestElevator.getId());
        } else {
            pendingRequests.add(request);
            System.out.println("Request queued (no suitable elevator available)");
        }
    }

    private Elevator findBestElevator(Request request) {
        Elevator bestElevator = null;
        int minDistance = Integer.MAX_VALUE;
        int minLoad = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (elevator.canTakeRequest(request)) {
                int distance = Math.abs(elevator.getCurrentFloor() - request.getPickupFloor());
                int load = elevator.getNumDestinations();

                // Prefer closer elevators, then less loaded ones
                if (distance < minDistance || (distance == minDistance && load < minLoad)) {
                    minDistance = distance;
                    minLoad = load;
                    bestElevator = elevator;
                }
            }
        }

        return bestElevator;
    }

    public void step() {

        // Move all elevators
        StringBuilder move = new StringBuilder("| ");
        StringBuilder arrivals = new StringBuilder("| ");


        for (Elevator elevator : elevators) {
            String[] res = elevator.move();

            if (res != null) {
                if (!res[0].isEmpty()) {
                    String paddedString = String.format("%-34s", res[0]);
                    move.append(paddedString).append(" | ");
                }
                if (!res[1].isEmpty()) {
                    String paddedString = String.format("%-34s", res[1]);
                    arrivals.append(paddedString).append(" | ");
                }
            }
        }

        if (!move.toString().equals("| ")) System.out.println(move);
        if (!arrivals.toString().equals("| ")) System.out.println(arrivals);

        // Process pending requests
        processPendingRequests();
    }

    private void processPendingRequests() {
        Iterator<Request> iterator = pendingRequests.iterator();

        while (iterator.hasNext()) {
            Request request = iterator.next();
            Elevator elevator = findBestElevator(request);

            if (elevator != null) {
                elevator.addRequest(request);
                System.out.println("Pending request (Floor " + request.getPickupFloor() +
                        " → " + request.getDropoffFloor() + ") assigned to Elevator " +
                        elevator.getId());
                iterator.remove();
            }
        }
    }

    public void printStatus() {
        System.out.println("\n=== ELEVATOR STATUS ===");
        for (Elevator elevator : elevators) {
            System.out.println("Elevator " + elevator.getId() +
                    ": Floor " + elevator.getCurrentFloor() +
                    ", Direction: " + elevator.getCurrentDirection() +
                    ", Destinations: " + elevator.getDestinationFloors());
        }
        System.out.println("Pending requests: " + pendingRequests.size());
        System.out.println("=======================\n");
    }
}

// Main simulation class
public class ElevatorSimulation {
    public static void main(String[] args) throws InterruptedException {
        // Create a building with 2 elevators and 10 floors
        ElevatorController controller = new ElevatorController(2, 10);

        System.out.println("=== ELEVATOR SIMULATION STARTED ===\n");

        // Simulate various requests (pickup floor, dropoff floor)
        controller.requestElevator(5, 9);   // Person at floor 5 going to 9
        controller.requestElevator(3, 1);   // Person at floor 3 going to 1
        controller.requestElevator(7, 10);  // Person at floor 7 going to 10

        // Run simulation for 30 steps
        for (int i = 0; i < 30; i++) {
            Thread.sleep(1000); // Wait 1 second between steps
            controller.step();

            // Add more requests during simulation
            if (i == 5) {
                controller.requestElevator(2, 8);  // Person at floor 2 going to 8
            }
            if (i == 10) {
                controller.requestElevator(6, 2);  // Person at floor 6 going to 2
            }
            if (i == 15) {
                controller.requestElevator(1, 7);  // Person at floor 1 going to 7
            }

            if (i % 5 == 0) {
                controller.printStatus();
            }
        }

        controller.printStatus();
        System.out.println("=== SIMULATION ENDED ===");
    }
}