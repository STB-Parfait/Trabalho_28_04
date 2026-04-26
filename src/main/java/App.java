import com.sun.net.httpserver.HttpServer;
import config.DatabaseInitializer;
import controllers.AppointmentHandler;
import controllers.ContactHandler;
import controllers.DocsHandler;
import controllers.ReportHandler;
import repositories.AppointmentRepository;
import repositories.ContactRepository;
import services.AppointmentService;
import services.ContactService;
import services.ReportService;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("APP_PORT", "8000"));

        DatabaseInitializer.initialize();

        ContactRepository contactRepository = new ContactRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        ContactService contactService = new ContactService(contactRepository);
        AppointmentService appointmentService = new AppointmentService(appointmentRepository, contactRepository);
        ReportService reportService = new ReportService(port);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/contacts", new ContactHandler(contactService));
        server.createContext("/appointments", new AppointmentHandler(appointmentService));
        server.createContext("/reports/agenda-summary", new ReportHandler(reportService));
        server.createContext("/openapi.json", DocsHandler.openapi());
        server.createContext("/swagger", DocsHandler.swagger());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("Agenda API running at http://localhost:" + port);
    }
}
