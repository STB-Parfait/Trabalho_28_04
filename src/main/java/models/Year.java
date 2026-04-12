package models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class Year {
    private Integer referenceId;
    private boolean leapYear;
    private HashMap<Integer, Month> months = new HashMap<>();

    public Year(Integer referenceId){
        this.referenceId = referenceId;
        this.leapYear = this.isLeapYear(referenceId);
        for(int i = 1; i < (12 + 1); i++){
            this.months.put(i, new Month(i, this.leapYear));
        }
    }

    public void addNewEvent(LocalDateTime referenceDate, String name){
        if (months.containsKey(referenceDate.getMonth().getValue())){
            months.get(referenceDate.getMonthValue()).addNewEvent(referenceDate, name);
        } else{
            throw new Error("{ Invalid [month]. }");
        }
    }

    public List<Event> getAllEvents(List<Event> eventsList){
        for(Month m : months.values()){
            m.getAllEvents(eventsList);
        }
        return eventsList;
    }

    private boolean isLeapYear(Integer year){
        String yearString = year.toString();
        char firstChar = yearString.charAt(2);
        char secondChar = yearString.charAt(3);
        String number = String.join(Character.toString(firstChar), Character.toString(secondChar));
        Integer trueNumber = Integer.parseInt(number);

        return (trueNumber % 4) == 0;
    }
    public boolean isLeapYear(){
        return this.leapYear;
    }

    public void showAllMonths(){
        for(Month m : months.values()){
            IO.println("\n---\n");
            IO.println("[month]:");
            m.printId();
            IO.println("[days inside]:");
            m.printDays();
        }
    }
    public void printId(){
        IO.println("[ano]");
        IO.println(this.referenceId);
    }
}
