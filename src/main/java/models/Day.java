package models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class Day {
    private Integer referenceId;
    private HashMap<Integer, Hour> hours = new HashMap<>();

    public Day(Integer referenceId){
        this.referenceId = referenceId;
        for(int i = 1; i < (23 + 1); i++){
            this.hours.put(i, new Hour(i));
        }
    }

    public void printSimpleDay(){
        IO.println(this.referenceId);
    }

    public void addNewEvent(LocalDateTime referenceDate, String name){
        if (hours.containsKey(referenceDate.getHour())){
            hours.get(referenceDate.getHour()).addNewEvent(referenceDate, name);
        } else{
            throw new Error("{ Invalid [hour]. }");
        }
    }

    public List<Event> getAllEvents(List<Event> eventsList){
        for(Hour h : hours.values()){
            if (h.getEvent() != null){
                eventsList.add(h.getEvent());
            }
        }
        return eventsList;
    }
}
