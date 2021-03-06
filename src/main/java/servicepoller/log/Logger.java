package servicepoller.log;

public interface Logger {

    void info(String message);

    void error(Throwable tx);
}
