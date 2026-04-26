package models;

public class AgendaSummary {
    private int totalContacts;
    private int totalAppointments;
    private int scheduledAppointments;
    private int todayAppointments;
    private int pastAppointments;
    private int canceledAppointments;

    public int getTotalContacts() {
        return totalContacts;
    }

    public void setTotalContacts(int totalContacts) {
        this.totalContacts = totalContacts;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public int getScheduledAppointments() {
        return scheduledAppointments;
    }

    public void setScheduledAppointments(int scheduledAppointments) {
        this.scheduledAppointments = scheduledAppointments;
    }

    public int getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(int todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    public int getPastAppointments() {
        return pastAppointments;
    }

    public void setPastAppointments(int pastAppointments) {
        this.pastAppointments = pastAppointments;
    }

    public int getCanceledAppointments() {
        return canceledAppointments;
    }

    public void setCanceledAppointments(int canceledAppointments) {
        this.canceledAppointments = canceledAppointments;
    }
}
