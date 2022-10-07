package other;

import io.swagger.client.model.LiftRide;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Producer implements Runnable{
    private final int num = 200000;
    private final BlockingQueue<LiftRideEvent> buffer;
    private final CountDownLatch countDownLatch;
    public Producer(BlockingQueue<LiftRideEvent> buffer, CountDownLatch countDownLatch){

        this.buffer = buffer;
        this.countDownLatch = countDownLatch;
    }
    public void run(){
        Random random = new Random();
        for(int i=0; i<num; i++){
            int time = random.nextInt(360)+1;
            int liftID = random.nextInt(40)+1;
            LiftRide lr = new LiftRide().time(time).liftID(liftID);
            //Integer resortID = random.nextInt(10)+1;
            Integer resortID = 8;
            String seasonID = "2022";
            String dayID = "1";
            Integer skierID = random.nextInt(100000)+1;

            try {
                buffer.put(new LiftRideEvent(resortID,seasonID,dayID,skierID,lr));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //countDownLatch.countDown();
    }

}
