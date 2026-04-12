package models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class Month {
    private Integer referenceId;
    private HashMap<Integer, Day> days = new HashMap<>();

    public Month(Integer referenceId, boolean leapYear){
        this.referenceId = referenceId;

        Integer numberOfDays = 0;
        if (referenceId < 8){
            if ((referenceId % 2) == 1){
                numberOfDays = 31;
            } else{
                if (referenceId == 2 && leapYear){
                    numberOfDays = 29;
                } else if (referenceId == 2){
                    numberOfDays = 28;
                } else{
                    numberOfDays = 30;
                }
            }
        } else{
            if ((referenceId % 2) == 0){
                numberOfDays = 31;
            } else{
                if (referenceId == 2 && leapYear){
                    numberOfDays = 29;
                } else if (referenceId == 2){
                    numberOfDays = 28;
                } else{
                    numberOfDays = 30;
                }
            }
        }

        for(int i = 1; i < (numberOfDays + 1); i++){
            this.days.put(i, new Day(i));
        }
    }

    public void printId(){
        IO.println(this.referenceId);
    }
    public void printDays(){
        for(Day d : days.values()){
            d.printSimpleDay();
        }
    }

    public void addNewEvent(LocalDateTime referenceDate, String name){
        if (days.containsKey(referenceDate.getDayOfMonth())){
            days.get(referenceDate.getDayOfMonth()).addNewEvent(referenceDate, name);
        } else{
            throw new Error("{ Invalid [day]. }");
        }
    }

    public List<Event> getAllEvents(List<Event> eventsList){
        for(Day d : days.values()){
            d.getAllEvents(eventsList);
        }
        return eventsList;
    }
}
