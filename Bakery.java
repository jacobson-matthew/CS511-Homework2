import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.*;

public class Bakery implements Runnable {
    private static final int TOTAL_CUSTOMERS = 200;
    private static final int ALLOWED_CUSTOMERS = 50;
    private static final int FULL_BREAD = 20;
    private Map<BreadType, Integer> availableBread;
    private ExecutorService executor;
    private float sales = 0;

    // TODO
    Semaphore cashier = new Semaphore(4);    
    Semaphore[] shelves = new Semaphore[3];
    shelves[0] = new Semaphore(1); // Rye bread
    shelves[1] = new Semaphore(1); // Sourdough bread
    shelves[2] = new Semaphore(1); // wonder bread
    Semaphore store = new Semaphore(ALLOWED_CUSTOMERS);
    Semaphore stocking = new Semaphore(0);
    
    // public void checkout(Customer cust){
    //     try{
    //     Thread.sleep(cust.getTime());
    //     }catch(InterruptedException e){
    //         System.out.println(e);
    //     }
    // }

    /**
     * Remove a loaf from the available breads and restock if necessary
     */
    public void takeBread(BreadType bread) {
        int num;
        if(bread == RYE){
            num = 0;
        }else if(bread == SOURDOUGH){
            num = 1;
        }else if(bread == WONDER){
            num = 2;
        }


        int breadLeft = availableBread.get(bread);
        if (breadLeft > 0) {
            availableBread.put(bread, breadLeft - 1);
        } else {
            System.out.println("No " + bread.toString() + " bread left! Restocking...");
            // restock by preventing access to the bread stand for some time
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            availableBread.put(bread, FULL_BREAD - 1);
        }
    }

    /**
     * Add to the total sales
     */
    public void addSales(float value) {
        sales += value;
    }

    /**
     * Run all customers in a fixed thread pool
     */
    public void run() {
        availableBread = new ConcurrentHashMap<BreadType, Integer>();
        availableBread.put(BreadType.RYE, FULL_BREAD);
        availableBread.put(BreadType.SOURDOUGH, FULL_BREAD);
        availableBread.put(BreadType.WONDER, FULL_BREAD);

        // TODO
        //bakery
        
        //obj to make 
        ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_CUSTOMERS);
        //make our 200 customers

        for (int i = 0; i < TOTAL_CUSTOMERS; i++){
            executorService.execute(new Customer(this));
        }
        executorService.shutdown();
        // take customers from outside of store and bring them in
        

    

        //take bread 


        //go to cashier


        //exit protocol
        


    
    
    }
    
}