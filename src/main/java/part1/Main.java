package part1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import other.LiftRideEvent;
import other.SuccessCnt;
import other.Producer;

public class Main {

    public static final int TOTAL = 200000;
    public static final int NUMOFTHREADS = 200;
    public static final int NUMOFREQPERTHREAD = 1000;
    public static final int CAPACITY = 10000;
    public static void main(String[] args) throws InterruptedException {

        SuccessCnt successCnt = new SuccessCnt();

        CountDownLatch gate = new CountDownLatch(1);
        CountDownLatch countDownLatch = new CountDownLatch(NUMOFTHREADS);
        BlockingQueue<LiftRideEvent> bq = new LinkedBlockingQueue<>(CAPACITY);
        Producer producer = new Producer(bq,null);

        long startTime = System.currentTimeMillis();

        new Thread(producer).start();

        for(int i=0; i<32; i++){
            Consumer consumer = new Consumer(NUMOFREQPERTHREAD,bq,successCnt,countDownLatch,gate);
            new Thread(consumer).start();
        }

        gate.await();
        int i;
        for(i=0; i<168; i++){
            Consumer consumer = new Consumer(NUMOFREQPERTHREAD,bq,successCnt,countDownLatch);
            new Thread(consumer).start();
        }

        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        double throughPut = TOTAL/(double)time;
        System.out.println("Number of successful requests sent: "+successCnt.getSuccess());
        System.out.println("Number of unsuccessful requests: "+successCnt.getFail());
        System.out.println("Total run time: "+time);
        System.out.println("Total through put: " + throughPut);
    }

}