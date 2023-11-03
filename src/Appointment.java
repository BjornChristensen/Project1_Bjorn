import java.time.LocalDateTime;
import java.util.ArrayList;

public class Appointment implements Comparable<Appointment> {
    String customer;
    LocalDateTime datetime;
    double price;
    boolean paid;
    ArrayList<Product> products=new ArrayList<Product>();

    Appointment(String cust, LocalDateTime dt){     // Constructor
        customer =cust;
        datetime=dt;
        price=130;
        paid=false;
    }

    // Update an existing Appointment with products and payment info
    void menu(){
        while (true){
            System.out.println();
            System.out.println("Aftale menu");
            System.out.println(this);                           // show info for ths appointments
            for (Product p: products) System.out.println(p);    // and it´s products
            System.out.println("  Tryk 1 for betal");           // show menu
            System.out.println("  Tryk 2 for tilkøb");
            System.out.println("  Tryk 0 for hovedmenu");
            int n=Main.inputInt(0,2);
            if (n==0) break;                                    // return to main menu
            if (n==1) paid=true;                                // register payment
            if (n==2) {
                Product p= Product.menu();                      // choose something from product menu
                if (p!=null) {                                  // a product was chosen
                    products.add(p);                            // add the product to products list
                    products.sort(null);                        // keep the list sorted by product name
                    paid=false;                                 // customer must pay again
                }
            }
        }
    }

    // Price for haircut and Products together
    double totalPrice(){
        double total=price;
        for (Product p:products) total=total+p.price;
        return total;
    }

    @Override
    public String toString() {
        return datetime.toLocalDate()+" "+datetime.toLocalTime()+" "+customer+" Betalt: "+(paid?totalPrice():"Nej");
    }

    // compare appointments by their datetime
    public int compareTo(Appointment a) {
        return datetime.compareTo(a.datetime);
    }
}
