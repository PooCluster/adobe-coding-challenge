package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static class Lead {
        public String id;
        public String email;
        public String firstName;
        public String lastName;
        public String address;
        public String entryDate;

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

    private static JSONObject fileNameToJSONObject(String fileName) throws IOException {
        if (new File(fileName).exists()) {
            String jsonString = Files.readString(Path.of(fileName));
            return new JSONObject(jsonString);
        } else {
            throw new FileNotFoundException("Could not find file: " + fileName);
        }
    }

    private static List<Lead> jsonObjectToList(JSONObject jsonObject) {
        List<Lead> leads = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("leads");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonLead = jsonArray.getJSONObject(i);
            leads.add(new Lead(
                jsonLead.getString("_id"),
                jsonLead.getString("email"),
                jsonLead.getString("firstName"),
                jsonLead.getString("lastName"),
                jsonLead.getString("address"),
                jsonLead.getString("entryDate")
            ));
        }
        return leads;
    }

    private static List<Lead> removeDuplicates(List<Lead> leads) {
        List<Lead> noDupsLeads = new ArrayList<>();
        Map<String, Lead> seenIds = new HashMap<>();
        Map<String, Lead> seenEmails = new HashMap<>();

        // TODO: fill out logic doing a one pass of leads list finding dups (ids/emails)

        return noDupsLeads;
    }

    private static void outputResults(List<Lead> leads) {
        String jsonString = "{\"leads\":" + leads.toString() + "\n}";
        System.out.println(jsonString);
    }

    public static void main(String[] args) throws IOException {
        // file to remove duplicates; TODO: take it from String[] args
        String fileName = "leads.json";

        System.out.println("[STEP 1]: Getting JSON data from file: " + fileName);
        JSONObject jsonObject = fileNameToJSONObject(fileName);

        System.out.println("[STEP 2]: Transform JSONObject to a list of Leads");
        List<Lead> leads = jsonObjectToList(jsonObject);

        System.out.println("[STEP 3]: Remove duplicates from the list of Leads");
        List<Lead> noDupsLeads = removeDuplicates(leads);

        System.out.println("[STEP 4]: Output the results!");
        outputResults(leads);
    }
}