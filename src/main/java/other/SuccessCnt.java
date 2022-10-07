package other;

public class SuccessCnt {
    private int success = 0;
    private int fail = 0;
    public SuccessCnt(){}

    public int getSuccess() {
        return success;
    }

    synchronized public void incSuccess() {
        this.success++;
    }

    public int getFail() {
        return fail;
    }

    synchronized public void incFail() {
        this.fail = fail;
    }
}
