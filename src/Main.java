import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    static Scanner keyboard=new Scanner(System.in);

    public static void main(String[] args) {
        Product.initList();
        Salon salon=new Salon("Harrys Salon ", "hairyharry");
        salon.holidays.add(LocalDate.of(2023, 10, 16));     // Efterårsferie
        salon.holidays.add(LocalDate.of(2023, 11, 17));
        salon.holidays.add(LocalDate.of(2023, 10, 18));
        salon.holidays.add(LocalDate.of(2023, 10, 19));
        salon.holidays.add(LocalDate.of(2023, 10, 20));
        salon.holidays.add(LocalDate.of(2023, 11, 3));
        salon.appointments.add(new Appointment("Peter", LocalDateTime.of(2023,11,1,10,00)));
        salon.appointments.add(new Appointment("Kaj", LocalDateTime.of(2023,11,1,10,30)));
        salon.appointments.add(new Appointment("Ole", LocalDateTime.of(2023,11,2,11,00)));
        salon.appointments.add(new Appointment("Ole", LocalDateTime.of(2023,11,4,11,30)));
        salon.appointments.add(new Appointment("Ole", LocalDateTime.of(2023,11,5,11,00)));
        salon.appointments.add(new Appointment("Ole", LocalDateTime.of(2023,11,6,17,00)));
        salon.appointments.add(new Appointment("Ole", LocalDateTime.of(2023,11,7,17,30)));
        salon.menu();

        // Test
        // salon.appointments.get(1).menu();
        // Salon.inputTimeSlot();
        // salon.inputWorkDate();
        // System.out.println(salon.inputInt(1,5));
    } // main

    /******************************************** Helper methods *****************************************************/
    // Read an integer in [low,high] from keyboard
    static int inputInt(int low, int high) {
        int result;
        while (true){
            System.out.print("Indtast et tal mellem "+low+" og "+high+": ");
            try {
                result=keyboard.nextInt();
                if (low<=result && result<=high) return result;
            } catch (InputMismatchException e) {
                keyboard.nextLine();    // flush input buffer
            }
        }
    }

    // Ask user for y/n. y will return true, all other input will return false.
    static boolean yesNo(){
        System.out.print("(y/n)");
        String input=" ";
        try {
            input=keyboard.next();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        return input.charAt(0)=='y';
    }

    static void waitForEnter(){
        System.out.print("Tryk Enter");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

} // Main
