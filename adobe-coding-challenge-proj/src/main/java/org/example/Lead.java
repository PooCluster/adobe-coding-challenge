package org.example;

// Lead stores the data to be stored within a lead JSON object for this particular program.
public class Lead {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String entryDate;

    // default constructor cannot be used
    private Lead() {}

    public Lead(String id, String email, String firstName, String lastName,
                String address, String entryDate) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.entryDate = entryDate;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getEntryDate() {
        return entryDate;
    }

    @Override
    public String toString() {
        // JSON-formatted
        return "{\n" +
                "\"_id\": \"" + id + "\",\n" +
                "\"email\": \"" + email + "\",\n" +
                "\"firstName\": \"" + firstName + "\",\n" +
                "\"lastName\": \"" + lastName + "\",\n" +
                "\"ddress\": \"" + address + "\",\n" +
                "\"entryDate\": \"" + entryDate + "\"\n}";
    }
}