import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class Bakery implements Runnable {
    private static final int TOTAL_CUSTOMERS = 200;
    private static final int ALLOWED_CUSTOMERS = 50;
    private static final int FULL_BREAD = 20;
    private Map<BreadType, Integer> availableBread;
    private ExecutorService executor;
    private float sales = 0;
	
	private int rcount = 0;
	private int scount = 0;
	private int wcount = 0;
	private int purch = 0;
	public int purcheck = 0;
	public int totalitemsadded = 0;

    // TODO
	
	Semaphore cashier = new Semaphore(4);    
	Semaphore [] shelves = new Semaphore [] {new Semaphore(1),new Semaphore(1),new Semaphore(1)};
    Semaphore store = new Semaphore(ALLOWED_CUSTOMERS);
    Semaphore stocking = new Semaphore(0);
	Semaphore allcustdone = new Semaphore(0);
	Semaphore adjustsale = new Semaphore(1);
	Semaphore restock = new Semaphore(1);


    /**
     * Remove a loaf from the available breads and restock if necessary
     */
    public void takeBread(BreadType bread) {
		//-------------------------------------------------------------------------------
		switch (bread){
			case SOURDOUGH:
				purch++;
				scount++;
				break;
			case RYE:     
				purch++;
				rcount++;
				break;
			case WONDER:  
				purch++;
				wcount++;
				break;
		}
		//-------------------------------------------------------------------------------
		
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
			
			try {
                restock.acquire();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            availableBread.put(bread, FULL_BREAD - 1);
			restock.release();
        }
    }

    /**
     * Add to the total sales
     */
    public void addSales(float value) {
		try{
			adjustsale.acquire();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
        }
		
        sales += value;
		adjustsale.release();
		
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
		
		ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_CUSTOMERS);
        //make our 200 customers

        for (int i = 0; i < TOTAL_CUSTOMERS; i++){
            executorService.execute(new Customer(this));
        }
        executorService.shutdown();
		
		
		
		for (int i = 0; i < TOTAL_CUSTOMERS; i++){
			try{
				allcustdone.acquire();
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		System.out.println("----------------------------------------");
		System.out.printf("Total Sales: %.2f\n", sales);
		System.out.println("----------------------------------------");
		System.out.println("Sourdough:    " + scount);
		System.out.println("Rye:          " + rcount);
		System.out.println("Wonder Bread: " + wcount);
		System.out.println("----------------------------------------");
		
		/*System.out.println("Total Purchases: " + purch);
		System.out.println("Items Added to Cart: " + purcheck);
		System.out.println("Items added to sales: " + totalitemsadded);*/
		//System.out.printf("%.2f\n", (scount * 4.99 + rcount * 3.99 + wcount * 5.99));
		
		
    }
}