import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * SentriCampus -- Stage 1 (Core classes + OOP), single-file edition.
 *
 * This is the SAME code as the multi-file project, merged into one file so
 * it can be pasted into an online compiler (JDoodle, OneCompiler, etc.) or
 * submitted as a single .java file. Java only allows ONE public top-level
 * class per file, and it must match the filename -- so everything except
 * Main (which has main()) is declared WITHOUT the `public` keyword here.
 * That's a file-level visibility change only; every class still has all
 * its normal public methods and fields, and the code behaves identically
 * to the multi-file version.
 *
 * For a real submission, use the proper multi-file, multi-package project
 * instead -- that's what a Java course actually expects to see.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== SentriCampus: Stage 1 Model Layer Demo ===\n");

        User aisha = new User("U-001", "Aisha Khan", Role.STUDENT, "aisha@vit.edu");
        User rao = new User("U-002", "Dr. Rao", Role.FACULTY, "rao@vit.edu");
        User securityDesk = new User("U-003", "Gate Security Desk", Role.SECURITY, "security@vit.edu");

        Emergency e1 = new MedicalEmergency("Student collapsed near canteen", "Cafeteria",
                Severity.HIGH, aisha.getId(), 1, true);
        Emergency e2 = new FireEmergency("Smoke reported in chemistry lab", "Academic Block",
                Severity.CRITICAL, rao.getId(), 4);
        Emergency e3 = new SecurityEmergency("Unauthorized entry at gate 2", "Admin Block",
                Severity.MEDIUM, securityDesk.getId(), "Intrusion");
        Emergency e4 = new AccidentEmergency("Two-vehicle collision at parking lot", "Sports Complex",
                Severity.HIGH, securityDesk.getId(), 2);
        Emergency e5 = new UtilityEmergency("Power outage in Hostel Block C", "Hostel Block",
                Severity.LOW, rao.getId(), "Power");

        printEmergency(e1);
        printEmergency(e2);
        printEmergency(e3);
        printEmergency(e4);
        printEmergency(e5);

        Resource ambulance = new Ambulance("Ambulance-01", "Cafeteria");
        Resource firefighter = new Firefighter("FF-Team-A", "Academic Block");
        Resource kit = new FirstAidKit("FirstAidKit-Cafeteria", "Cafeteria");

        System.out.println("\n--- Compatibility checks (polymorphism + instanceof) ---");
        checkCompatibility(ambulance, e1);
        checkCompatibility(firefighter, e2);
        checkCompatibility(kit, e1); // false: e1 is life-threatening, the kit can't handle it

        System.out.println("\n--- Assigning the ambulance to e1 ---");
        if (ambulance.assignTo(e1)) {
            e1.updateStatus(EmergencyStatus.ASSIGNED);
            System.out.println(ambulance);
            System.out.println(e1.generateReport());
        }

        System.out.println("\n--- Registered users ---");
        System.out.println(aisha);
        System.out.println(rao);
        System.out.println(securityDesk);
    }

    private static void printEmergency(Emergency emergency) {
        // 'emergency' is declared as the abstract base type, but the two
        // method calls below run each subclass's own overridden logic --
        // this is polymorphism, not just inheritance.
        System.out.println(emergency.generateReport()
                + " | Requires: " + emergency.getRequiredResourceType()
                + " | Priority score: " + emergency.getPriorityScore());
    }

    private static void checkCompatibility(Resource resource, Emergency emergency) {
        boolean compatible = resource.isCompatibleWith(emergency);
        System.out.println(resource.getName() + " compatible with " + emergency.getId() + "? " + compatible);
    }
}

// ===================== enums =====================

/**
 * The five kinds of emergency SentriCampus handles. Stored on every
 * Emergency (set once, via the subclass's constructor) so later code -- the
 * Stage 2 statistics array, for instance -- can switch/index on it without
 * needing an instanceof check.
 */
enum EmergencyType {
    MEDICAL,
    FIRE,
    SECURITY,
    ACCIDENT,
    UTILITY
}

/**
 * How urgent an emergency is. Each level carries a numeric "weight" that
 * feeds directly into Emergency.getPriorityScore() -- severity isn't just a
 * label, it drives dispatch order.
 */
enum Severity {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    private final int weight;

    Severity(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}

/** Lifecycle states an Emergency moves through from report to closure. */
enum EmergencyStatus {
    REPORTED,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}

/** Availability state of a Resource at any given moment. */
enum ResourceStatus {
    AVAILABLE,
    ASSIGNED,
    OFF_DUTY
}

/** Who a User is, for access/role-based menu behaviour in later stages. */
enum Role {
    STUDENT,
    FACULTY,
    SECURITY,
    ADMIN
}

// ===================== interface =====================

/**
 * Anything capable of sending an Emergency into the dispatch pipeline.
 * No implementer yet -- EmergencyManager implements this in Stage 2, once
 * the service layer exists.
 */
interface Dispatchable {
    void dispatch(Emergency emergency);
}

// ===================== Emergency hierarchy =====================

/**
 * Base type for every kind of campus emergency.
 *
 * Concepts demonstrated: abstract class + abstract methods, encapsulation
 * (private final fields with controlled access), the 'this' keyword,
 * 'final' fields, a meaningful switch statement (getPriorityBand), and
 * String formatting. Method overriding and overloading live in the five
 * subclasses below.
 */
abstract class Emergency {

    // Simple incrementing id for now. Revisited for thread-safety in Stage 3
    // once several threads can create Emergencies at once.
    private static int idCounter = 1000;

    private final String id;
    private final String description;
    private final String location;
    private final Severity severity;
    private final EmergencyType type;
    private final LocalDateTime reportedAt;
    private final String reporterId;
    private EmergencyStatus status;

    protected Emergency(EmergencyType type, String description, String location, Severity severity, String reporterId) {
        this.id = "EMG-" + (idCounter++);
        this.type = type;
        this.description = description;
        this.location = location;
        this.severity = severity;
        this.reporterId = reporterId;
        this.reportedAt = LocalDateTime.now();
        this.status = EmergencyStatus.REPORTED;
    }

    /** Which resource type (e.g. "MEDICAL") this emergency needs. Each subclass defines this. */
    public abstract String getRequiredResourceType();

    /** Higher number = should be dispatched sooner. Each subtype defines its own formula. */
    public abstract int getPriorityScore();

    /**
     * The only way to change status from outside the class -- callers can't
     * reach in and set the field directly, which is the point of keeping it
     * private.
     */
    public void updateStatus(EmergencyStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * Turns the raw severity enum into a human-readable label used in
     * reports. A genuine switch statement, not one added just to tick a
     * syllabus box -- generateReport() actually calls this.
     */
    public String getPriorityBand() {
        switch (severity) {
            case LOW:
                return "Routine";
            case MEDIUM:
                return "Elevated";
            case HIGH:
                return "Urgent";
            case CRITICAL:
                return "Critical - immediate response";
            default:
                return "Unknown";
        }
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public Severity getSeverity() { return severity; }
    public EmergencyType getType() { return type; }
    public LocalDateTime getReportedAt() { return reportedAt; }
    public String getReporterId() { return reporterId; }
    public EmergencyStatus getStatus() { return status; }

    public String generateReport() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.format(
                "[%s] %s (%s) | %s | %s | Location: %s | Status: %s | Reported by %s at %s",
                id, getClass().getSimpleName(), type, description, getPriorityBand(), location, status,
                reporterId, reportedAt.format(fmt));
    }

    @Override
    public String toString() {
        return generateReport();
    }
}

/**
 * A medical incident. Demonstrates method overloading: the 4-arg
 * constructor covers the common case and delegates via this(...) to the
 * full 6-arg constructor for callers who need to specify patient count and
 * whether it's life-threatening.
 */
class MedicalEmergency extends Emergency {

    private final int patientCount;
    private final boolean lifeThreatening;

    public MedicalEmergency(String description, String location, Severity severity, String reporterId) {
        this(description, location, severity, reporterId, 1, false);
    }

    public MedicalEmergency(String description, String location, Severity severity, String reporterId,
                             int patientCount, boolean lifeThreatening) {
        super(EmergencyType.MEDICAL, description, location, severity, reporterId);
        this.patientCount = patientCount;
        this.lifeThreatening = lifeThreatening;
    }

    @Override
    public String getRequiredResourceType() {
        return "MEDICAL";
    }

    @Override
    public int getPriorityScore() {
        int score = getSeverity().getWeight() * 10 + patientCount * 3;
        if (lifeThreatening) score += 20;
        return score;
    }

    public int getPatientCount() { return patientCount; }
    public boolean isLifeThreatening() { return lifeThreatening; }
}

/** A fire incident. fireLevel (1-5) scales the priority score alongside severity. */
class FireEmergency extends Emergency {

    private final int fireLevel;

    public FireEmergency(String description, String location, Severity severity, String reporterId) {
        this(description, location, severity, reporterId, 2);
    }

    public FireEmergency(String description, String location, Severity severity, String reporterId, int fireLevel) {
        super(EmergencyType.FIRE, description, location, severity, reporterId);
        this.fireLevel = fireLevel;
    }

    @Override
    public String getRequiredResourceType() {
        return "FIRE";
    }

    @Override
    public int getPriorityScore() {
        return getSeverity().getWeight() * 10 + fireLevel * 5;
    }

    public int getFireLevel() { return fireLevel; }
}

/** A security incident, e.g. intrusion, theft, assault. */
class SecurityEmergency extends Emergency {

    private final String threatType;

    public SecurityEmergency(String description, String location, Severity severity, String reporterId) {
        this(description, location, severity, reporterId, "Unspecified");
    }

    public SecurityEmergency(String description, String location, Severity severity, String reporterId, String threatType) {
        super(EmergencyType.SECURITY, description, location, severity, reporterId);
        this.threatType = threatType;
    }

    @Override
    public String getRequiredResourceType() {
        return "SECURITY";
    }

    @Override
    public int getPriorityScore() {
        int base = getSeverity().getWeight() * 10;
        return threatType.equalsIgnoreCase("Assault") ? base + 15 : base;
    }

    public String getThreatType() { return threatType; }
}

/** A physical accident, e.g. a vehicle collision or a fall. */
class AccidentEmergency extends Emergency {

    private final int vehiclesInvolved;

    public AccidentEmergency(String description, String location, Severity severity, String reporterId) {
        this(description, location, severity, reporterId, 1);
    }

    public AccidentEmergency(String description, String location, Severity severity, String reporterId, int vehiclesInvolved) {
        super(EmergencyType.ACCIDENT, description, location, severity, reporterId);
        this.vehiclesInvolved = vehiclesInvolved;
    }

    @Override
    public String getRequiredResourceType() {
        return "ACCIDENT";
    }

    @Override
    public int getPriorityScore() {
        return getSeverity().getWeight() * 10 + vehiclesInvolved * 4;
    }

    public int getVehiclesInvolved() { return vehiclesInvolved; }
}

/** A utility failure, e.g. power, water, internet, HVAC. */
class UtilityEmergency extends Emergency {

    private final String utilityType;

    public UtilityEmergency(String description, String location, Severity severity, String reporterId) {
        this(description, location, severity, reporterId, "Power");
    }

    public UtilityEmergency(String description, String location, Severity severity, String reporterId, String utilityType) {
        super(EmergencyType.UTILITY, description, location, severity, reporterId);
        this.utilityType = utilityType;
    }

    @Override
    public String getRequiredResourceType() {
        return "MAINTENANCE";
    }

    @Override
    public int getPriorityScore() {
        return getSeverity().getWeight() * 10;
    }

    public String getUtilityType() { return utilityType; }
}

// ===================== Resource hierarchy =====================

/**
 * Base type for every dispatchable resource.
 *
 * Concepts demonstrated: abstract class + abstract method, constructor
 * overloading (two constructors chained with this(...)), encapsulation.
 */
abstract class Resource {

    private static int idCounter = 1;

    private final String id;
    private final String name;
    private String zone;
    private ResourceStatus status;

    /** Convenience constructor: auto-generates an id. */
    protected Resource(String name, String zone) {
        this(generateId(), name, zone);
    }

    /** Full constructor: caller supplies an explicit id (e.g. when loading from the database). */
    protected Resource(String id, String name, String zone) {
        this.id = id;
        this.name = name;
        this.zone = zone;
        this.status = ResourceStatus.AVAILABLE;
    }

    private static String generateId() {
        return "RES-" + (idCounter++);
    }

    /** Can this resource handle the given emergency type? Each subclass decides via instanceof. */
    public abstract boolean isCompatibleWith(Emergency emergency);

    // NOT synchronized yet. Once EmergencyTask threads (Stage 3) can call
    // this concurrently, two threads could both read AVAILABLE before either
    // writes ASSIGNED -- that race is exactly what 'synchronized' will fix.
    // Left plain here on purpose so the "before" behaviour is visible later.
    public boolean assignTo(Emergency emergency) {
        if (status != ResourceStatus.AVAILABLE) {
            return false;
        }
        this.status = ResourceStatus.ASSIGNED;
        return true;
    }

    public void release() {
        this.status = ResourceStatus.AVAILABLE;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    public ResourceStatus getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s in %s", id, name, getClass().getSimpleName(), status, zone);
    }
}

/** Handles medical emergencies and accidents. */
class Ambulance extends Resource {

    public Ambulance(String name, String zone) {
        super(name, zone);
    }

    @Override
    public boolean isCompatibleWith(Emergency emergency) {
        return emergency instanceof MedicalEmergency || emergency instanceof AccidentEmergency;
    }
}

/** Handles security incidents and assists with accidents. */
class SecurityOfficer extends Resource {

    public SecurityOfficer(String name, String zone) {
        super(name, zone);
    }

    @Override
    public boolean isCompatibleWith(Emergency emergency) {
        return emergency instanceof SecurityEmergency || emergency instanceof AccidentEmergency;
    }
}

/** Handles fire emergencies. */
class Firefighter extends Resource {

    public Firefighter(String name, String zone) {
        super(name, zone);
    }

    @Override
    public boolean isCompatibleWith(Emergency emergency) {
        return emergency instanceof FireEmergency;
    }
}

/** Handles utility failures (power, water, internet, HVAC). */
class MaintenanceStaff extends Resource {

    public MaintenanceStaff(String name, String zone) {
        super(name, zone);
    }

    @Override
    public boolean isCompatibleWith(Emergency emergency) {
        return emergency instanceof UtilityEmergency;
    }
}

/**
 * A stocked first-aid station. Only handles minor medical cases -- this is
 * where instanceof plus a downcast is genuinely needed, since "is this
 * life-threatening?" is a field that only exists on MedicalEmergency, not
 * on the base Emergency type.
 */
class FirstAidKit extends Resource {

    public FirstAidKit(String name, String zone) {
        super(name, zone);
    }

    @Override
    public boolean isCompatibleWith(Emergency emergency) {
        if (!(emergency instanceof MedicalEmergency)) {
            return false;
        }
        MedicalEmergency medical = (MedicalEmergency) emergency;
        return !medical.isLifeThreatening();
    }
}

// ===================== User =====================

/** A person who can report emergencies or (if SECURITY/ADMIN) manage resources. */
class User {

    private final String id;
    private final String name;
    private final Role role;
    private final String contact;

    public User(String id, String name, Role role, String contact) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.contact = contact;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Role getRole() { return role; }
    public String getContact() { return contact; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s [%s]", name, id, role, contact);
    }
}
