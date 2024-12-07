package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    // A pair class holding a resulting list of leads and the change logs required to get the resulting list.
    public static class LeadChangeLogPair {
        public List<Lead> leads;
        public List<ChangeLog> changeLogs;

        // default constructor cannot be used
        private LeadChangeLogPair() {}

        public LeadChangeLogPair(List<Lead> leads, List<ChangeLog> changeLogs) {
            this.leads = leads;
            this.changeLogs = changeLogs;
        }
    }

    /*
     * Creates a JSON object from a file name.
     *
     * @param fileName The JSON-formatted file.
     * @return The JSON object.
     * @throws IOException If there is an error with file reading.
     * @throws FileNotFoundException If the file does not exist.
     */
    private static JSONObject fileNameToJSONObject(String fileName) throws IOException {
        if (new File(fileName).exists()) {
            String jsonString = Files.readString(Path.of(fileName));
            return new JSONObject(jsonString);
        } else {
            throw new FileNotFoundException("Could not find file: " + fileName);
        }
    }

    /*
     * Transforms a leads-containing JSON object into a list of leads.
     *
     * @param leadsJsonObject The lead-containing JSON object.
     * @return The list of leads.
     */
    private static List<Lead> jsonObjectToList(JSONObject leadsJsonObject) {
        List<Lead> leads = new ArrayList<>();
        JSONArray jsonArray = leadsJsonObject.getJSONArray("leads");
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

    /*
     * Prints list of change logs in both the output window and a file based relevant to the original file.
     *
     * @param leads The list of leads.
     * @param inFileName The original JSON file name.
     */
    private static void outputChangeLogs(List<ChangeLog> changeLogs, String inFileName) throws IOException {
        String outputString = "CHANGE LOGS:\n";
        for (ChangeLog changeLog : changeLogs) {
            outputString += changeLog.toString() + "\n";
        }
        System.out.println(outputString);

        String outFileName = inFileName.substring(0, inFileName.lastIndexOf('.')) + "-changelog.txt";
        Files.write(Paths.get(outFileName), outputString.getBytes());
    }

    /*
     * This algorithm takes a list of leads, and produces a list of leads without duplicates along
     * with change logs recording the necessary changes to needed to get from the list of leads to
     * a list of leads without duplicates.
     *
     * Here is a further breakdown of the algorithm based on my assumptions of the problem.
     * Duplicate IDs are dups. Duplicate emails are dups. *Both* are unique in that
     * an ID can only occur *once* and an email can only occur *once* no matter which
     * ID has what email. If we analyze the resulting list of Leads by ID, the resulting
     * ID list is a set (each element is unique). Likewise, if we analyze the resulting
     * list of Leads by email, the resulting email list is a set.
     *
     * Example:
     * [
     *     {id: "1", email: "test1@example.com", date: "2024-12-01", ... },
     *     {id: "2", email: "test1@example.com", date: "2024-12-01", ... },
     *     {id: "1", email: "test2@example.com", date: "2024-12-01", ... },
     *     {id: "2", email: "test2@example.com", date: "2024-12-01", ... }
     * ]
     * should become
     * [
     *     {id: "2", email: "test1@example.com", date: "2024-12-01", ... },
     *     {id: "1", email: "test2@example.com", date: "2024-12-01", ... }
     * ]
     *
     * Explanation:
     * The first two rows are duplicates, because same email. Between the first two rows,
     * the second row is kept, because it comes later in the list. The third row is kept,
     * because the only row kept so far is the second row, and the ids and emails are unique
     * from each other. The fourth row is discarded, because it has the same id as the second
     * row (first kept row) and same email as the third row (second kept row).
     *
     * @param leads The original list of leads that may have duplicates as defined above.
     * @return The resulting list of leads without duplicates and the changelogs needed to get
     *      from the original list of leads to the resulting list of leads.
     */
    private static LeadChangeLogPair removeDuplicates(List<Lead> leads) {
        List<Lead> noDupsLeads = new ArrayList<>();
        List<ChangeLog> changeLogs = new ArrayList<>();
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
                            changeLogs.add(new ChangeLog(commitedLead, lead));
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

        return new LeadChangeLogPair(noDupsLeads, changeLogs);
    }

    /*
     * Writes the result list of leads to a file in JSON format relevant to the original JSON file.
     *
     * @param leads The list of leads.
     * @param inFileName The original JSON file name.
     */
    private static void outputResults(List<Lead> leads, String inFileName) throws IOException {
        String outFileName = inFileName.substring(0, inFileName.lastIndexOf('.')) + "-dupless.json";
        String jsonString = "{\"leads\":" + leads.toString() + "\n}";
        Files.write(Paths.get(outFileName), jsonString.getBytes());
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Provide a json file as a parameter to the program. ex) \"leads.json\"");
        }

        String fileName = args[0];

        System.out.println("[STEP 1]: Getting JSON data from file: " + fileName);
        JSONObject leadsJsonObject = fileNameToJSONObject(fileName);

        System.out.println("[STEP 2]: Transform JSONObject to a list of Leads");
        List<Lead> leads = jsonObjectToList(leadsJsonObject);

        System.out.println("[STEP 3]: Remove duplicates from the list of Leads");
        LeadChangeLogPair leadChangelogPair = removeDuplicates(leads);
        List<Lead> noDupsLeads = leadChangelogPair.leads;
        List<ChangeLog> changeLogs = leadChangelogPair.changeLogs;

        System.out.println("[STEP 4]: Output the results!");
        outputChangeLogs(changeLogs, fileName);
        outputResults(noDupsLeads, fileName);
    }
}