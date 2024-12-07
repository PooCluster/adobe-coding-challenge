package org.example;

import org.json.JSONObject;

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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", id);
        jsonObject.put("email", email);
        jsonObject.put("firstName", firstName);
        jsonObject.put("lastName", lastName);
        jsonObject.put("address", address);
        jsonObject.put("entryDate", entryDate);
        return jsonObject;
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