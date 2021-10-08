import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

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
        // TODO
		this.bakery = bakery;
        this.rnd = new Random();
        this.shoppingCart = new ArrayList<BreadType>();
        this.shopTime = 500 + rnd.nextInt(500);
        this.checkoutTime = 200 + rnd.nextInt(300);
		fillShoppingCart();
    }

    /**
     * Run tasks for the customer
     */
    public void run() {
        // TODO
		//go into the store
        
			try{
				bakery.store.acquire();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Customer " + hashCode() + " has started shopping");   
			//get bread
			//System.out.println("shopping...");
			
			try{
				Thread.sleep(shopTime);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < shoppingCart.size(); i++){
				int num = 0;
				switch (shoppingCart.get(i)){
					case SOURDOUGH:
						//System.out.println("item " + i + ": SOURDOUGH");					
						num = 0;
						break;
					case RYE:     
						//System.out.println("item " + i + ": RYE");						
						num = 1;
						break;
					case WONDER:  
						//System.out.println("item " + i + ": WONDER");
						num = 2;
						break;
				}
				
				try{
					bakery.shelves[num].acquire();
				}catch (InterruptedException e) {
					e.printStackTrace();
				}	
				
				//System.out.println("using shelf: " + num);
				System.out.println("Customer " + hashCode() + " has taken bread type: " + num + "."); 
				bakery.takeBread(shoppingCart.get(i));
				
				bakery.shelves[num].release();

			}
			
			//check out
			try{
				bakery.cashier.acquire();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//System.out.println("using cashier");
			
			try{
				Thread.sleep(checkoutTime);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Customer " + hashCode() + " has checked out."); 
			bakery.addSales(getItemsValue());
			bakery.cashier.release();

			//leave store
			bakery.store.release();
			
			System.out.println("Customer " + hashCode() + " has left the store."); 
			
			bakery.allcustdone.release();
			
			
		
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
		bakery.purcheck++;
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
			bakery.totalitemsadded++;
            value += bread.getPrice();
        }
        return value;
    }
}