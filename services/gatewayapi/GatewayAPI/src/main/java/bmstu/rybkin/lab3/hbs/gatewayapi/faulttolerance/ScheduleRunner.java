package bmstu.rybkin.lab3.hbs.gatewayapi.faulttolerance;

import java.util.Timer;
import java.util.TimerTask;

public class ScheduleRunner {

    private int timeout = 10000;

    private Timer timer = new Timer("Schedule");

    private TimerTask timerTask;

    public ScheduleRunner(TimerTask timerTask) {

        timeout = 10000;
        this.timerTask = timerTask;

    }

    public ScheduleRunner(int timeout, TimerTask timerTask) {

        this.timeout = timeout;
        this.timerTask = timerTask;

    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void startTasks() {

        timer.schedule(timerTask, 0, timeout);

    }

    public void taskComplete() {

        timer.cancel();
        timer.purge();

    }

}
