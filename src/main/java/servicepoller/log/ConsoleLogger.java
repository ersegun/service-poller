package servicepoller.log;

public class ConsoleLogger implements Logger{

    @Override
    public void info(String message) {
        System.out.println(message);
    }

    @Override
    public void error(Throwable tx) {
        System.out.println(tx.toString());
    }
}
