package models;

import java.time.LocalDateTime;

public class Event {
    private String name;
    private LocalDateTime referenceDate;

    public Event(String name, LocalDateTime referenceDate){
        this.name = name;
        this.referenceDate = referenceDate;
    }

    public void showName(){
        IO.println(name);
    }
    public void showReferenceDate(){
        IO.println(referenceDate.getDayOfMonth() + "/" + referenceDate.getMonthValue() + "/" + referenceDate.getYear() + " at " + referenceDate.getHour() + ":00");
    }
}
