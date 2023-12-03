import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class LoadGenerator {

    private final String target;
    private final int frequency;
    private int totalRequests;
    private int totalFailures;
    private double totalResponseTime;

    public LoadGenerator(String target, int frequency) {
        this.target = target;
        this.frequency = frequency;
        this.totalRequests = 0;
        this.totalFailures = 0;
        this.totalResponseTime = 0;
    }

    public void makeRequest() {
        long startTime = System.nanoTime();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.connect();

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                totalFailures++;
            }

            connection.disconnect();
        } catch (IOException e) {
            totalFailures++;
        } finally {
            long endTime = System.nanoTime();
            totalResponseTime += (endTime - startTime) / 1e9;
        }
    }

    public void run() {
        while (true) {
            long startEpoch = System.nanoTime();

            for (int i = 0; i < frequency; i++) {
                makeRequest();
                totalRequests++;
            }

            long endEpoch = System.nanoTime();
            double elapsedSeconds = (endEpoch - startEpoch) / 1e9;

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dtf.format(LocalDateTime.now());
            System.out.println("Load Info: " + formattedDate);
            System.out.println("Failures/Requests: " + totalFailures + " / " + totalRequests);
            System.out.println("Average Response: " + (totalResponseTime / totalRequests) + "s\n");

            try {
                TimeUnit.SECONDS.sleep(Math.max(0, 5 - (long) elapsedSeconds));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("The load generator was interrupted!");
                break;
            }
        }
    }

    public static void main(String[] args) {
        String targetAddress = "http://192.168.0.102:30000/primecheck";
        int requestFrequency = 1; // requests per second
        LoadGenerator generator = new LoadGenerator(targetAddress, requestFrequency);
        generator.run();
    }
}
