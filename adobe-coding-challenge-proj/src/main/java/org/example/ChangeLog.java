package org.example;

public class ChangeLog {
    private Lead from;
    private Lead to;

    // default constructor cannot be used
    private ChangeLog() {}

    public ChangeLog(Lead from, Lead to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        String res = "--- START CHANGE LOG ---\n";
        res += "from " + from.toString() + "\n";
        res += "to   " + to.toString() + "\n";
        if (!from.getId().equals(to.getId())) {
            res += "id:    " + from.getId() + " -> " + to.getId() + "\n";
        }
        if (!from.getEmail().equals(to.getEmail())) {
            res += "email: " + from.getEmail() + " -> " + to.getEmail() + "\n";
        }
        res += "---  END CHANGE LOG  ---";
        return res;
    }
}
