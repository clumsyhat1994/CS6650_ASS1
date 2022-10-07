package part2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
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
        List<Entry> list = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch gate = new CountDownLatch(1);
        CountDownLatch countDownLatch = new CountDownLatch(NUMOFTHREADS);
        BlockingQueue<LiftRideEvent> bq = new LinkedBlockingQueue<>(CAPACITY);
        Producer producer = new Producer(bq,null);

        long startTime = System.currentTimeMillis();

        new Thread(producer).start();

        for(int i=0; i<32; i++){
            Consumer consumer = new Consumer(NUMOFREQPERTHREAD,bq,successCnt,list,countDownLatch,gate);
            new Thread(consumer).start();
        }

        gate.await();

        for(int i=0; i<168; i++){
            Consumer consumer = new Consumer(NUMOFREQPERTHREAD,bq,successCnt,list,countDownLatch);
            new Thread(consumer).start();
        }

        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        double throughPut = TOTAL/(double)time;

        File file = new File("log.csv");

        //System.out.println("Length of list: "+list.size());

        long[] arr = new long[TOTAL];
        long sum = 0;
        int i = 0;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("Start Time,"+"Resquest Type,"+"Latency,"+"ResponseCode");
            for(Entry entry:list){
                sum += entry.getLatency();
                arr[i] = entry.getLatency();
                writer.newLine();
                writer.write(entry.getStartTime()+","+entry.getRequestType()+","+entry.getLatency()+","+entry.getResponseCode());
                i++;
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Arrays.sort(arr);
        long median = (arr[TOTAL/2-1]+arr[TOTAL/2])/2;
        long mean = sum/TOTAL;
        long p99 = arr[(int)Math.ceil((double)99/100.0 * TOTAL)-1];

        System.out.println("Number of successful requests sent: "+successCnt.getSuccess());
        System.out.println("Number of unsuccessful requests: "+successCnt.getFail());
        System.out.println("Total run time: "+time);
        System.out.println("Total through put: " + throughPut);

        System.out.println("Mean response time: "+mean);
        System.out.println("Median response time:" + median);
        System.out.println("99th percentile: "+p99);
        System.out.println("Min response time: "+arr[0]);
        System.out.println("Max response time: "+arr[TOTAL-1]);
    }

}