# SentriCampus – Stage 1

## What is SentriCampus?

SentriCampus is a Java-based model for handling emergencies on a college campus. The idea is simple: when something goes wrong, the system should be able to record what happened, understand what type of incident it is, identify the urgency, and connect it with a suitable campus resource.

This version is the first stage of the project. It mainly focuses on the core classes and object-oriented programming concepts rather than a complete application with a database or user interface.

## What this version demonstrates

The project currently includes:

- Different emergency types such as medical, fire, security, accident, and utility incidents.
- Severity levels ranging from LOW to CRITICAL.
- Emergency status tracking from reporting through closure.
- Campus resources such as ambulances, firefighters, security officers, maintenance staff, and first-aid kits.
- Users with roles such as student, faculty, security, and admin.
- Automatic IDs for emergencies and resources.
- Priority scores that help represent how urgently an incident should be handled.
- Compatibility checks between emergencies and available resources.
- Basic emergency reporting and formatted output.

## OOP concepts used

This project was designed around the main Java OOP concepts:

### 1. Abstraction

`Emergency` and `Resource` are abstract classes. They contain common information and behaviour while leaving certain decisions to their child classes.

For example, every emergency has a description, location, severity, and status, but each emergency type can calculate its priority differently.

### 2. Inheritance

Specific emergency classes extend `Emergency`, including:

- `MedicalEmergency`
- `FireEmergency`
- `SecurityEmergency`
- `AccidentEmergency`
- `UtilityEmergency`

The resource hierarchy works in a similar way with classes such as `Ambulance`, `Firefighter`, `SecurityOfficer`, `MaintenanceStaff`, and `FirstAidKit`.

### 3. Encapsulation

Important fields are kept private and are accessed through methods. This prevents other parts of the program from directly changing internal state without going through the class interface.

### 4. Polymorphism

The program stores different emergency objects using the common `Emergency` type. When methods such as `generateReport()`, `getRequiredResourceType()`, and `getPriorityScore()` are called, the implementation belonging to the actual emergency class is used.

### 5. Method overloading

Several classes provide constructors with different parameter lists. A shorter constructor can delegate to a more detailed constructor using `this(...)`.

### 6. Method overriding

Child classes provide their own implementations of methods declared by their parent classes. This is particularly useful for calculating resource compatibility and emergency priority.

## How priority works

Each severity level has a numeric weight:

| Severity | Weight |
|---|---:|
| LOW | 1 |
| MEDIUM | 2 |
| HIGH | 3 |
| CRITICAL | 4 |

The individual emergency classes then use this value along with their own information.

For example, a medical emergency can take the number of patients and whether the situation is life-threatening into account. A fire emergency uses its fire level as an additional factor.

The exact formulas are implemented in the individual emergency classes.

## Resource matching

Resources are not treated as interchangeable. Each resource checks whether it can deal with a particular emergency.

Examples:

- An ambulance can handle medical emergencies and accidents.
- A firefighter handles fire emergencies.
- A security officer handles security incidents and can assist with accidents.
- Maintenance staff handle utility failures.
- A first-aid kit can be used for non-life-threatening medical cases.

This makes the resource-dispatch idea more realistic than simply assigning any available resource to any emergency.

## Project flow

The current `Main` class creates a few sample users and emergencies. It then:

1. Displays the emergency reports.
2. Creates several campus resources.
3. Checks whether selected resources are suitable for particular emergencies.
4. Assigns an available ambulance to a medical emergency.
5. Updates the emergency status to `ASSIGNED`.
6. Prints the resulting resource and emergency information.

The code also contains a `Dispatchable` interface. It is intended to become more useful in the later stage when the service/dispatch layer is added.

## Running the program

The project is currently provided as a single Java file so that it can be copied directly into an online Java compiler or run easily from a basic Java setup.

If the file is saved as:

`Main.java`

it can be compiled and executed with:

```bash
javac Main.java
java Main
```

## Current scope and future development

This is intentionally a Stage 1 implementation. It focuses on the model layer and OOP structure.

Possible future additions include:

- A dedicated emergency management/service layer.
- Real dispatch logic.
- Better resource allocation.
- Persistent storage or database support.
- Multithreaded emergency processing.
- Authentication and role-based access.
- A graphical or web-based interface.
- Statistics and emergency analytics.
- Stronger validation and error handling.

## Project structure

The current single-file version contains:

- Enums for emergency types, severity, statuses, and user roles.
- A `Dispatchable` interface.
- The `Emergency` class hierarchy.
- The `Resource` class hierarchy.
- The `User` class.
- A `Main` class used for demonstration.

For a larger submission, these classes can be separated into individual Java files and organized into packages.

## Note

The project is intended as an educational implementation of an emergency-management model using Java OOP. The current version is a demonstration of the underlying design rather than a production-ready campus emergency system.
