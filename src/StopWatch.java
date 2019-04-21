public class StopWatch {
    private long startTime;

    // store time of when stopwatch is initialized/created
    public StopWatch() {
        startTime = System.currentTimeMillis();
    }

    // get time difference
    public double getElapsedTime() {
        long endTime = System.currentTimeMillis();
        return (double) (endTime - startTime) / (1000);
    }
}