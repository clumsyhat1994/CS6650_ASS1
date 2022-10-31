package part2;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import other.LiftRideEvent;
import other.SuccessCnt;

public class Consumer implements Runnable {
    private final int num;
    private final BlockingQueue<LiftRideEvent> buffer;
    private final CountDownLatch countDownLatch;
    private final CountDownLatch gate;

    private final SuccessCnt successCnt;

    private final List<Entry> list;
    //private final String BASEPATH = "http://35.85.56.163:8080/Servlet_war";
    //private final String BASEPATH = "http://54.185.251.91:8080/ASS2_Servlet_war";
    private final String BASEPATH = "http://ASS2LoadBalancer-1346720230.us-west-2.elb.amazonaws.com/ASS2_Servlet_war";
    public Consumer(int num, BlockingQueue<LiftRideEvent> buffer, SuccessCnt successCnt, List<Entry> list, CountDownLatch countDownLatch){
        this.num = num;
        this.buffer = buffer;
        this.countDownLatch = countDownLatch;
        this.gate = null;
        this.successCnt = successCnt;
        this.list = list;
    }

    public Consumer(int num, BlockingQueue<LiftRideEvent> buffer, SuccessCnt successCnt,  List<Entry> list, CountDownLatch countDownLatch, CountDownLatch gate){
        this.num = num;
        this.buffer = buffer;
        this.countDownLatch = countDownLatch;
        this.gate = gate;
        this.successCnt = successCnt;
        this.list=list;
    }
    public void run(){
        for(int i=0; i<num; i++){
            LiftRideEvent liftRideEvent;
            try {
                liftRideEvent = buffer.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(BASEPATH);
            SkiersApi apiInstance = new SkiersApi(apiClient);

            int status = 400;
            int cnt = 5;

            long start = System.currentTimeMillis();
            while(status!=200&&cnt>0){

                try {
                    status = post(apiInstance,liftRideEvent).getStatusCode();

                } catch (ApiException e) {
                    e.printStackTrace();
                    status = e.getCode();
                }

                cnt--;
            }

            long end = System.currentTimeMillis();
            long latency = end - start;
            list.add(new Entry(start,"POST",latency,status));

            if(cnt==0&&status!=200) {
                this.successCnt.incFail();
                throw new RuntimeException("Unable to connect to server");
            }
            this.successCnt.incSuccess();
        }
        countDownLatch.countDown();
        if(gate!=null) gate.countDown();


    }

    private ApiResponse post(SkiersApi apiInstance, LiftRideEvent liftRideEvent) throws ApiException {

        return apiInstance.writeNewLiftRideWithHttpInfo(liftRideEvent.getLiftRide(), liftRideEvent.getResortID(), liftRideEvent.getSeasonID(), liftRideEvent.getDayID(), liftRideEvent.getSkierID());

    }
}
