package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Agenda {
    private HashMap<Integer, Year> years = new HashMap<>();

    public Agenda(){
        LocalDate now = LocalDate.now();
        Integer currentYear = now.getYear();
        years.put(currentYear, new Year(currentYear));
    }

    public void debugAgendaCreation(){
        for(Year y : years.values()){
            y.printId();
            y.showAllMonths();
        }
    }

    public void addNewEvent(LocalDateTime referenceDate, String name){
        if (years.containsKey(referenceDate.getYear())){
            years.get(referenceDate.getYear()).addNewEvent(referenceDate, name);
        } else{
            throw new Error("{ Invalid [year]. }");
        }
    }

    public List<Event> getAllEvents(){
        List<Event> eventsList = new ArrayList<>();
        for(Year y : years.values()){
            y.getAllEvents(eventsList);
        }
        return eventsList;
    }
}
