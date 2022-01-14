package Common;

import java.util.concurrent.atomic.AtomicInteger;

public class Timer implements Runnable
{
    private AtomicInteger minutes;
    private AtomicInteger duration;

    public Timer(int duration)
    {
        this.minutes = new AtomicInteger(0);
        this.duration = new AtomicInteger(duration);
    }

    public int getDuration()
    {
        return duration.get();
    }

    public void setDuration(int duration)
    {
        this.duration.set(duration);
    }

    public int getTime()
    {
        return minutes.get();
    }

    @Override
    public void run()
    {
        minutes.incrementAndGet();
    }
}
