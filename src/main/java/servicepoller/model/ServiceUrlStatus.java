package servicepoller.model;

import java.util.Arrays;

public enum ServiceUrlStatus {

    UNKNOWN(0),
    OK(1),
    FAILED(2);

    public final Integer status;

    ServiceUrlStatus(Integer status) {
        this.status = status;
    }

    public static ServiceUrlStatus getServiceUrlStatus(int status) {
        return Arrays.stream(values())
                .filter(l -> l.status.equals(status))
                .findFirst().get();
    }
}


