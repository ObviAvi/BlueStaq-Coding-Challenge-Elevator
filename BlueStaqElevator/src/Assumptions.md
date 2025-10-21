## Assumptions Made

### 1. Building Structure
- **Single building**: System manages elevators in one building only
- **Floor numbering**: Floors numbered starting from 1 (no floor 0 or basement levels)
- **Fixed floors**: Number of floors is constant and defined at initialization (10 floors in demo)
- **All elevators service all floors**: No express elevators or floor restrictions

### 2. Elevator Behavior
- **FIFO Floor Servicing**: Elevators visit floors in the exact order requests are received (no optimization like sorting intermediate requests)
- **Movement speed**: Each floor transition takes one "step" (approximately 1 second in demo)
- **No door operation delay**: Doors open/close instantly when arriving at a floor
- **Initial position**: All elevators start at floor 1
- **Direction determined dynamically**: Direction is set based on the next destination floor, job is completed before taking next request

### 3. Request Model
- **Complete trip specification**: Each request includes both pickup floor and  dropoff floor
- **Immutable requests**: Request objects are final/immutable once created
- **No request cancellation**: Once made, requests cannot be cancelled
- **No request modification**: Cannot change pickup or dropoff after request is created

### 4. Request Scheduling & Assignment
- **Nearest available elevator**: Assigns closest elevator that can take the request
- **Load balancing tie-breaker**: When distances are equal, assigns to elevator with fewer destinations
- **Direction-based assignment**: Only assigns requests to elevators going in the same direction (or idle)
- **Pending queue**: Requests that can't be immediately assigned are queued (FIFO)
- **Continuous reassignment**: Pending requests are re-evaluated every step

### 5. Capacity & Load
- **No capacity enforcement**: Elevator class accepts unlimited requests 
- **No weight simulation**: No concept of passenger weight or physical capacity limits
- **No passenger objects**: System doesn't model individual passengers
- **Instantaneous boarding**: People enter/exit instantly when elevator arrives

### 6. Movement & Physics
- **One floor per step**: Elevator moves exactly one floor per simulation step
- **No acceleration/deceleration**: constant speed between floors
- **Deterministic movement**: Always moves toward first destination in list
- **No destination sorting**: Visits floors in request order, not necessarily optimal order

### 7. Data Structures
- **List for destinations**: Uses `ArrayList<Integer>` for destination floors 
- **Queue for pending**: Uses `LinkedList<Request>` as FIFO queue for unassigned requests

## Features not Implemented

- **No passenger tracking**: System doesn't track individual passengers or their journey
- **No passenger limits**: Can theoretically have infinite passengers
- **No emergency stop**: No emergency button or system halt
- **No request grouping**: Doesn't combine multiple requests going to same floor
- **No intermediate pickup**: Doesn't pick up additional passengers along the route
- **Duplicate destinations possible**: If not checking for duplicates carefully, may visit same floor twice

## Design Patterns & Principles Used

1. **Object-Oriented Design**: Separate classes for Elevator, Request, Controller with clear responsibilities
2. **Encapsulation**: Private fields with public getter methods for controlled access
3. **Immutability**: Request objects are immutable (final fields)
4. **Controller Pattern**: Central ElevatorController manages all elevators and requests
5. **Queue Pattern**: FIFO pending request queue
6. **Iterator Pattern**: Safe removal from pending queue while iterating

## Design Trade-offs

### Simplicity vs. Efficiency
- **Chosen**: Simple FIFO floor visiting
- **Trade-off**: Potentially inefficient movement
- **Benefit**: Implementation is simple to understand and debug

### Assignment Strategy
- **Chosen**: Distance-first, then load-balancing
- **Benefit**: Good distribution of work between elevators
- **Limitation**: Because the destination of an elevator could be far away and there could be multiple destinations in between, assignment strategy might not be optimal. This is curbed somewhat by the implementation of multiple elevators as well as the chooseBestEvevator functionality.