import java.util.ArrayList;
import java.util.Scanner;
import java.time.*;
import java.time.format.DateTimeParseException;

// Constructor
public class Salon {
    static Scanner keyboard=new Scanner(System.in);
    String name;                                            // name of Salon
    String password;                                        // password for Salon
    String username;                                        // name of current user. null if no one is logged in
    ArrayList<Appointment> appointments=new ArrayList<Appointment>();
    ArrayList<LocalDate> holidays =new ArrayList<LocalDate>();

    Salon(String name, String password){
        this.name=name;
        this.password=password;
        username=null;
    }

    // Display the main menu, and retrieve user input.
    void menu(){
        char input;
        boolean keepGoing=true;
        do {
            System.out.println();
            System.out.println(name+ " - Hoved menu"+((username!=null) ? "   User: "+username : ""));
            System.out.println("  Tryk 1 for login");
            System.out.println("  Tryk 2 for logud");
            System.out.println("  Tryk 3 for ny aftale");
            System.out.println("  Tryk 4 for vis aftaler");
            System.out.println("  Tryk 5 for opdater aftale");
            System.out.println("  Tryk 6 for slet aftale");
            System.out.println("  Tryk 7 for registrer lukkedag");
            System.out.println("  Tryk 8 for vis lukkedage");
            System.out.println("  Tryk 9 for regnskab");
            System.out.println("  Tryk 0 for afslut");
            System.out.print("Indtast valg: ");
            input=keyboard.next().charAt(0);
            System.out.println();
            switch (input){
                case '1': login(); break;
                case '2': logout(); break;
                case '3': createAppointment(); break;
                case '4': showAppointments(); break;
                case '5': updateAppointment(); break;
                case '6': deleteAppointment(); break;
                case '7': registerHolyday(); break;
                case '8': printHolydays(); break;
                case '9': accounting(); break;
                case '0': keepGoing=false; break;
                default:  System.out.println("Ugyldigt indput. Prøv igen");
            }
            if (keepGoing) Main.waitForEnter();
        } while (keepGoing);
        System.out.println("Tak for i dag");
    }

    // Login. All usernames are valid. Password must match Salon.password.
    void login(){
        System.out.println("Login to "+name);
        System.out.print("Enter username: ");
        String input1=keyboard.next();

        boolean OK=false;
        while (!OK){
            System.out.print("Enter password: ");
            String input2=keyboard.next();
            if (input2.equals(password)) {
                username=input1;
                OK=true;
            } else {
                System.out.println("Sorry - wrong password.");
                System.out.println("Please try again");
            }
        } // while
    }

    // Logout. username is set to null to indicate that no one is logged in.
    void logout() {
        username=null;
        System.out.println("Du er nu logget ud");
    }

    // Create a new Appointment and add it to appointment list
    void createAppointment(){
        System.out.println(name+" - Ny aftale");
        LocalDate date=inputWorkDate();                           // user inputs a legal work date
//        LocalDate date=LocalDate.of(2023,11,1); // for test

        // Show appointments 5 days ahead excluding weekends but including holidays
        System.out.println("Tider         10:00 10:30 11:00 11:30 12:00 12:30 13:00 13:30 14:00 14:30 15:00 15:30 16:00 16:30 17:00 17:30");
        LocalDate d=date;                                               // first of the five dates
        LocalDate[] weekDays=new LocalDate[5];                          // array for all five weekdays
        int count=0;
        while (count<5) {
            if (!isWeekEnd(d)) {
                weekDays[count]=d;                                      // save weekdays
                System.out.println((count+1)+": "+d+" "+getDayplan(d)); // print the dayplan in one line
                count++;
            }
            d=d.plusDays(1);                                            // next day
        }

        // Choose date (enter an integer in [1,5]) and time for new appointment. Enter 0 to return to main menu
        LocalDateTime datetime;
        while (true){
            System.out.println("Vælg en af de viste dage. Tast 0 for hovedmenu");
            int n=Main.inputInt(0,5);                                   // user selects a date
            if (n==0) return;                                           // return to main menu
            LocalDate selected=weekDays[n-1];                           // date selected by user
            if (holidays.contains(selected)){                           // is it a holyday ?
                System.out.println("Valgte dato har vi lukket");        //
                continue;                                               // try again
            }
            LocalTime time=inputTimeSlot();                             // user inputs time
            datetime=LocalDateTime.of(selected, time);                  // combined datetime on selected day
            if (isBusy(datetime))
                System.out.println("Tidspunktet er optaget. Prøv igen");
            else break;                                                 // datetime is OK so we break the loop
        }

        // Enter customer name and add new appointment to appointments list
        System.out.print("Indtast kunde navn: ");
        String customer=keyboard.next();
        appointments.add(new Appointment(customer, datetime));
        appointments.sort(null);                                        // make sure appointments are chronological order
    }

    // A dayplan is a String that shows busy timeslots on a given day. A timeslot is 30 min.
    //  10:00 10:30 11:00 11:30 12:00 12:30 13:00 13:30 14:00 14:30 15:00 15:30 16:00 16:30 17:00 17:30
    // "xxxxx       xxxxx                         xxxxx
    // Above is a dayPlan where 10:00 11:00 13:30 are marked as busy
    String getDayplan(LocalDate date) {
        int len=16*6;                                               // 16 timeslots in a day, each takes up 6 chars
        String dayplan=" ".repeat(len);                             // dayplan filled with ' ' (spaces)
        String holiday="-".repeat(len);                             // dayplan filled with '-' indicates holiday
        if (holidays.contains(date)) return holiday;                // date is a holiday - no appointments
        char[] plan=dayplan.toCharArray();                          // Strings are immutable, but char[] can be modified
        for (Appointment a: appointmentsOn(date)) {                 // lookup appointments on this date
            LocalTime time=a.datetime.toLocalTime();                // timeslot of appointment a
            int offset=((time.getHour()-10)*2 + time.getMinute()/30)*6; // offset of timeslot in dayplan
            for (int i=0; i<5; i++) plan[offset+i]='x';             // mark the timeslot as busy
        }
        return String.copyValueOf(plan);
    }

    // Print all appointments on a given day
    void showAppointments(){
        System.out.println(name+" - Vis aftaler ");
        LocalDate date=inputDate();
        ArrayList<Appointment> list=appointmentsOn(date);
        if (list.isEmpty()) System.out.println("Ingen aftaler på valgte dato");
        for (Appointment a: list) System.out.println(a);
    }

    // Update an appointment input by user
    void updateAppointment(){
        System.out.println(name+" - Ret aftale ");
        Appointment appointment=inputAppointment();
        if (appointment!=null) appointment.menu();
    }

    // Delete an appointment input by user
    void deleteAppointment(){
        System.out.println(name+" - Slet aftale ");
        Appointment appointment=inputAppointment();
        if (appointment!=null) {
            appointments.remove(appointment);
            System.out.println("Aftalen er slettet");
        }
    }

    // Register a holiday. There is no check for appointments already registered
    void registerHolyday(){
        System.out.println(name+" - Registrer lukkedag ");
        LocalDate date=inputDate();
        if (!holidays.contains(date)) holidays.add(inputDate());
    }

    // Print all holidays
    void printHolydays(){
        System.out.println(name+" - Lukkedage");
        for (LocalDate d: holidays) System.out.println(d);
    }

    // Display accounting info for a given date
    void accounting(){
        System.out.println(name+" - Regnskab");
        if (username==null) {
            System.out.println("Login for at se regnskabet");
            return;
        }
        LocalDate date=inputWorkDate();
        ArrayList<Appointment> list=appointmentsOn(date);
        if (list.isEmpty()) System.out.println("Ingen aftaler på valgte dato");
        double total=0;
        for (Appointment a: list) {
            System.out.printf("%s %7.2f \n", a ,a.totalPrice());
            total=total+a.totalPrice();
        }
        System.out.printf("Ialt %7.2f \n", total);
    }

    /**************************************** Bookkeeping of appointment list *****************************************/
    // Find all appointments on a given LocalDate
    ArrayList<Appointment> appointmentsOn(LocalDate date) {
        ArrayList<Appointment> list=new ArrayList<Appointment>();
        for (Appointment a: appointments) {
            if (date.equals(a.datetime.toLocalDate())) {
                list.add(a);
            }
        }
        return list;
    }

    // Find an appointment at a given timeslot dt in appointment list. Return null if no appointment exists
    Appointment getAppointment(LocalDateTime dt) {
        for (Appointment a: appointments) {
            if (a.datetime.equals(dt)) return a;
        }
        return null;
    }

    // Check for an appointment on a given timeslot
    boolean isBusy(LocalDateTime dt) {
        return getAppointment(dt)!=null;
    }

    Appointment inputAppointment(){
        Appointment appointment;
        while (true) {
            LocalDate date=inputWorkDate();
            LocalTime time=inputTimeSlot();
            LocalDateTime datetime=LocalDateTime.of(date, time);
            appointment=getAppointment(datetime);                       // lookup appointment from appointments list
            if (appointment==null){                                     // it wasn´t there
                System.out.println("Beklager. Aftale ikke fundet");
                System.out.print("Vil du prøve igen ");
                if (Main.yesNo()==false) break;                         // try a new date/time ?
            } else break;;                                              // appointment was OK
        }
        return appointment;
    }

    /******************************************** Date and time methods ***********************************************/
    // Read a LocalDate that is not a weekend or holiday from keyboard
    LocalDate inputWorkDate(){
        LocalDate date;
        while (true){
            date=inputDate();
            if (isWeekEnd(date)) {
                System.out.println(date + " er en weekend");
                continue;
            }
            if (holidays.contains(date)) {
                System.out.println(date + " er en lukkedag");
                continue;
            }
            return date;
        } // while
    }

    // Read a LocalDate from keyboard
    LocalDate inputDate(){
        while (true){
            System.out.print("Indtast dato (åååå-mm-dd): ");
            String input=keyboard.next();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Fejl i dato. Prøv igen");
            }
        }
    }

    // Check if a LocalDate is a weekend
    boolean isWeekEnd(LocalDate date) {
        return  (date.getDayOfWeek()==DayOfWeek.SATURDAY || date.getDayOfWeek()==DayOfWeek.SUNDAY);
    }

    // Read a LocalTime in [10:00,17:30] in ½-hour steps from keyboard
    LocalTime inputTimeSlot(){
        LocalTime slot;
        while (true){
            slot=inputTime();
            if (10<=slot.getHour() && slot.getHour()<=17 && (slot.getMinute()==0 || slot.getMinute()==30))
                return slot;
            else
                System.out.println("Kun tider hver ½ timen mellem 10:00 og 17:30");
        }
    }

    // Read a LocalTime from keyboard
    LocalTime inputTime(){
        while (true){
            System.out.print("Indtast tid (tt:mm): ");
            String input=keyboard.next();
            try {
                return LocalTime.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Fejl i tid. Prøv igen");
            }
        }
    }
} // class Salon
