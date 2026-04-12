package models;

import java.time.LocalDateTime;

public class Hour {
    private Integer referenceId;
    private Event event;

    public Hour(Integer referenceId){
        this.referenceId = referenceId;
        this.event = null;
    }

    public void addNewEvent(LocalDateTime referenceDate, String name){
        if (this.event == null) {
            this.event = new Event(name, referenceDate);
        } else{
            throw new Error(" { Event already present as specified DateTime. } ");
        }
    }

    public Event getEvent(){
        return event;
    }
}
