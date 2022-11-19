package bmstu.rybkin.lab3.hbs.gatewayapi.faulttolerance;


import javax.validation.constraints.Min;
import java.util.Timer;
import java.util.TimerTask;

public class CircuitBreaker {

    private Timer timer = new Timer("RestartTimer");

    private final TimerTask timerTask;
    @Min(1)
    private int failureThreshold;

    private State state;

    private int recordedFailures;

    private int timeout;

    public CircuitBreaker(TimerTask timerTask) {

        failureThreshold = 10;
        state = State.CLOSED;
        recordedFailures = 0;
        timeout = 120000;
        this.timerTask = timerTask;

    }

    public CircuitBreaker(int failureThreshold, int timeout, TimerTask timerTask) {

        this.failureThreshold = failureThreshold;
        state = State.CLOSED;
        recordedFailures = 0;
        this.timeout = timeout;
        this.timerTask = timerTask;

    }

    public int getFailureThreshold() {
        return failureThreshold;
    }

    public void setFailureThreshold(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getRecordedFailures() {
        return recordedFailures;
    }

    public void setRecordedFailures(int recordedFailures) {
        this.recordedFailures = recordedFailures;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void requestSuccess() {

        if (state == State.HALF_OPEN) {

            recordedFailures = 0;
            state = State.CLOSED;

        }

    }

    public void requestFailure() {

        if (state == State.OPEN)
            return;
        if (state == State.HALF_OPEN) {

            state = State.OPEN;
            timer = new Timer("RestartTimer");
            timer.schedule(timerTask, timeout);
            return;

        }
        if (state == State.CLOSED) {

            recordedFailures = recordedFailures + 1;
            if (recordedFailures >= failureThreshold) {

                state = State.OPEN;
                timer = new Timer("RestartTimer");
                timer.schedule(timerTask, timeout);

            }

        }

    }

}
