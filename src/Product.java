import java.util.ArrayList;

public class Product implements Comparable<Product> {
    static ArrayList<Product> list=new ArrayList<Product>();    // static list of all possible products types
    String name;                                                // A single products name
    double price;                                               // and it´s price

    // Print a menu af products, let the user choose one
    static Product menu(){
        int i;
        System.out.println();
        System.out.println("Produkt menu");
        for (i=0; i<list.size(); i++) {
            System.out.println("  Tast "+(i+1)+" for "+ list.get(i));
        }
        System.out.println("  Tast 0 for Aftale menu");
        System.out.print("Vælg en af de viste produkter ");
        int n=Main.inputInt(0,i);
        if (n==0)
            return null;
        else return list.get(n-1);
    }

    // Initialize the list of Products. There is one item in the list for each Product type available
    // Must bee initialized from Main before use !
    static void initList(){
        list.add(new Product("Shampoo", 100));
        list.add(new Product("Hårbørste lille", 50));
        list.add(new Product("Hårbørste stor", 75));
        list.add(new Product("Barbersprit", 150));
    }

    Product(String name, double price){
        this.name=name;
        this.price=price;
    }

    @Override
    public String toString() {
        return String.format("%-15s %7.2f", name, price);
    }

    // Compare products by their name. Used when sorting the Product.list
    public int compareTo(Product p) {
        return name.compareTo(p.name);
    }
}

