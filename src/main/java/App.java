import models.Agenda;
import models.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class App {
    public void main(){

        Agenda a = new Agenda();

        List<Event> testList = new ArrayList<>();

        a.addNewEvent(LocalDateTime.of(2026, 4, 25, 12, 0), "niver");

        testList = a.getAllEvents();

        for(Event e : testList){
            IO.println("---");
            e.showName();
            e.showReferenceDate();
            IO.println("---");
        }

    }
}
