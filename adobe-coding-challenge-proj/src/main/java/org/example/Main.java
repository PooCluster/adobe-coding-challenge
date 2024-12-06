package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

        @Override
        public String toString() {
            return "Lead:{id:" + id + ",email:" + email +
                    ",firstName:" + firstName + ",lastName:" + lastName +
                    ",address:" + address + ",entryDate:" + entryDate + "}";
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

    public static void main(String[] args) throws IOException {
        // file to remove duplicates; TODO: take it from String[] args
        String fileName = "leads.json";

        System.out.println("[STEP 1]: Getting JSON data from file: " + fileName);
        JSONObject jsonObject = fileNameToJSONObject(fileName);

        System.out.println("[STEP 2]: Transform JSONObject to a list of Leads");
        List<Lead> leads = jsonObjectToList(jsonObject);
    }
}