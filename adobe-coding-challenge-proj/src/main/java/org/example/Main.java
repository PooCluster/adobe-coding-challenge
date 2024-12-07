package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

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
        /*
        HERE IS MY ASSUMPTION OF THE RULES:
        Duplicate IDs are dups. Duplicate emails are dups. *Both* are unique in that
        an ID can only occur *once* and an email can only occur *once* no matter which
        ID has what email. If we analyze the resulting list of Leads by ID, the resulting
        ID list is a set (each element is unique). Likewise, if we analyze the resulting
        list of Leads by email, the resulting email list is a set.

        Example:
        [
            {id: "1", email: "test1@example.com", date: "2024-12-01", ... },
            {id: "2", email: "test1@example.com", date: "2024-12-01", ... },
            {id: "1", email: "test2@example.com", date: "2024-12-01", ... },
            {id: "2", email: "test2@example.com", date: "2024-12-01", ... }
        ]
        should become
        [
            {id: "2", email: "test1@example.com", date: "2024-12-01", ... },
            {id: "1", email: "test2@example.com", date: "2024-12-01", ... }
        ]

        Explanation:
        The first two rows are duplicates, because same email. Between the first two rows,
        the second row is kept, because it comes later in the list. The third row is kept,
        because the only row kept so far is the second row, and the ids and emails are unique
        from each other. The fourth row is discarded, because it has the same id as the second
        row (first kept row) and same email as the third row (second kept row).
        */
        List<Lead> noDupsLeads = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        Set<String> seenEmails = new HashSet<>();

        for (Lead lead : leads) {
            String leadId = lead.getId();
            String leadEmail = lead.getEmail();
            String leadDate = lead.getEntryDate();

            boolean isIdDup = seenIds.contains(leadId);
            boolean isEmailDup = seenEmails.contains(leadEmail);
            boolean isDup = isIdDup || isEmailDup;
            if (isDup) {
                /*
                We can only replace a Lead in noDupsLeads iff one of the conditions is true:
                    1. id and email are equal
                    2. id is equal and new lead email is unique (!isEmailDup)
                    3. email is equal and new lead id is unique (!isIdDup)
                Cases 2 and 3 ensure that a new Lead we commit to does not break contract.
                Let's pick the most recent replaceable Lead (if any). If the new Lead is not
                replaceable due to date being older, keep searching through commited Leads
                by most recent.
                */
                boolean replaced = false;
                for (int i = noDupsLeads.size() - 1; !replaced && i >= 0; i--) {
                    Lead commitedLead = noDupsLeads.get(i);
                    String commitedLeadId = commitedLead.getId();
                    String commitedLeadEmail = commitedLead.getEmail();
                    String commitedLeadDate = commitedLead.getEntryDate();

                    boolean bothEqual = leadId.equals(commitedLeadId) && leadEmail.equals(commitedLeadEmail);
                    boolean idEqualEmailUnique = leadId.equals(commitedLeadId) && !isEmailDup;
                    boolean emailEqualIdUnique = leadEmail.equals(commitedLeadEmail) && !isIdDup;
                    if (bothEqual || idEqualEmailUnique || emailEqualIdUnique) {
                        // found a valid replaceable Lead, compare dates
                        if (commitedLeadDate.equals(leadDate) || commitedLeadDate.compareTo(leadDate) < 0) {
                            // prefer latest one in list (lead instead of commited), or date is newer
                            if (idEqualEmailUnique) {
                                // remove commited unique email and replace with new unique email
                                seenEmails.remove(commitedLeadEmail);
                                seenEmails.add(leadEmail);
                            } else if (emailEqualIdUnique) {
                                // remove commited unique id and replace with unique id
                                seenIds.remove(commitedLeadId);
                                seenIds.add(leadId);
                            } else {
                                // bothEqual: no-op
                            }
                            noDupsLeads.set(i, lead);
                            // TODO: handle log of changes (we're changing seenLeads)
                            replaced = true;
                        }
                    }
                }
            } else {
                // not a dup, new value (we will commit to it)
                seenIds.add(leadId);
                seenEmails.add(leadEmail);
                noDupsLeads.add(lead);
            }
        }

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
        outputResults(noDupsLeads);
    }
}