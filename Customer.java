import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Customer implements Runnable {
    private Bakery bakery;
    private Random rnd;
    private List<BreadType> shoppingCart;
    private int shopTime;
    private int checkoutTime;

    /**
     * Initialize a customer object and randomize its shopping cart
     */
    public Customer(Bakery bakery) {
        this.bakery = bakery;
        this.rnd = new Random();
        this.shoppingCart = new List<BreadType>();
        this.shopTime = 500 + rnd.nextInt(500);
        this.checkoutTime = 200 + rnd.nextInt(300);
    }

    /**
     * Run tasks for the customer
     */
    public void run() {
        //go into the store
        bakery.store.acquire();
           
        //get bread
        System.out.println("shopping...");
        Thread.sleep(shopTime);
        for (int i = 0; i < shoppingCart.size(); i++){
            int num;
            
            switch (shoppingCart[i]){
                case BreadType.SOURDOUGH: 
                    num = 0;
                case BreadType.RYE:       
                    num = 1;
                case BreadType.WONDER:    
                    num = 2;
                case:
                    System.out.println("not an option");
            }

            bakery.shelves[num].acquire();
            
            bakery.takeBread(shoppingCart[i]);
            bakery.shelves[num].release();

        }
        
        //check out
        bakery.cashier.acquire();
        Thread.sleep(checkoutTime);
        bakery.addSales(getItemsValue());
        bakery.cashier.release();

        //leave store
        bakery.store.release();
    }
    /**
     * Return a string representation of the customer
     */
    public String toString() {
        return "Customer " + hashCode() + ": shoppingCart=" + Arrays.toString(shoppingCart.toArray()) + ", shopTime=" + shopTime + ", checkoutTime=" + checkoutTime;
    }

    /**
     * Add a bread item to the customer's shopping cart
     */
    private boolean addItem(BreadType bread) {
        // do not allow more than 3 items, chooseItems() does not call more than 3 times
        if (shoppingCart.size() >= 3) {
            return false;
        }
        shoppingCart.add(bread);
        return true;
    }

    /**
     * Fill the customer's shopping cart with 1 to 3 random breads
     */
    private void fillShoppingCart() {
        int itemCnt = 1 + rnd.nextInt(3);
        while (itemCnt > 0) {
            addItem(BreadType.values()[rnd.nextInt(BreadType.values().length)]);
            itemCnt--;
        }
    }

    /**
     * Calculate the total value of the items in the customer's shopping cart
     */
    private float getItemsValue() {
        float value = 0;
        for (BreadType bread : shoppingCart) {
            value += bread.getPrice();
        }
        return value;
    }

    // public int getTime(){
    //     return checkoutTime;
    // }

}