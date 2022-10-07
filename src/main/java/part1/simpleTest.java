package part1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import other.LiftRideEvent;
import other.SuccessCnt;
import other.Producer;

public class simpleTest {
    public static final int TOTAL = 10000;
    public static void main(String[] args) throws InterruptedException {
        SuccessCnt successCnt = new SuccessCnt();

        CountDownLatch countDownLatch = new CountDownLatch(TOTAL);
        BlockingQueue<LiftRideEvent> bq = new LinkedBlockingQueue<>(1000);
        Producer producer = new Producer(bq,null);

        long startTime = System.currentTimeMillis();

        new Thread(producer).start();
        Consumer consumer = new Consumer(TOTAL,bq,successCnt,countDownLatch);
        new Thread(consumer).start();

        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        double throughput = TOTAL/(double)time;
        System.out.println("Number of successful requests sent: "+successCnt.getSuccess());
        System.out.println("Number of unsuccessful requests: "+successCnt.getFail());
        System.out.println("Total run time: "+time);
        System.out.println("A single request roughly take: " + (long)time/10000);
        System.out.println("Throughput: "+ throughput);
    }

}
