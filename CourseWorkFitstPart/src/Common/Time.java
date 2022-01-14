package Common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;

@JsonAutoDetect
public class Time implements Comparable<Time>
{
    private int minutes;

    public Time()
    {
        minutes = 0;
    }

    public Time(int minutes)
    {
        setTime(minutes);
    }

    public Time(int days, int hours, int minutes)
    {
        setTime(days, hours, minutes);
    }

    public Time(Time time)
    {
        setTime(time);
    }

    private int getMinutes()
    {
        return minutes % 60;
    }

    private int getHours()
    {
        return (minutes / 60) % 24;
    }

    private int getDays()
    {
        return minutes / 1440;
    }

    public void setTime(int minutes)
    {
        this.minutes = Math.max(minutes, 0);
    }

    public int getTime()
    {
        return minutes;
    }

    public void setTime(int days, int hours, int minutes)
    {
        setTime(minutes + 60 * hours + 1440 * days);
    }

    public void setTime(Time time)
    {
        if (time != null)
        {
            minutes = time.minutes;
        }
    }

    public void addTime(Time time)
    {
        if (time != null)
        {
            minutes += time.minutes;
        }
    }

    public void addTime(int minutes)
    {
        this.minutes += minutes;
        if (this.minutes < 0)
        {
            this.minutes = 0;
        }
    }

    public static Time parseTime(String str)
    {
        try
        {
            int minutes = Integer.parseInt(str.substring(str.lastIndexOf(':') + 1));
            str = str.substring(0,str.lastIndexOf(':'));
            int hours = Integer.parseInt(str.substring(str.lastIndexOf(':') + 1));
            str = str.substring(0,str.lastIndexOf(':'));
            int days = Integer.parseInt(str);
            return new Time(days, hours, minutes);
        }
        catch (Exception e)
        {
            System.err.println("Illegal time format");
            throw e;
        }
    }

    @Override
    public String toString()
    {
        return getDays() + ":" + getHours() + ":" + getMinutes();
    }

    @Override
    public int compareTo(@NotNull Time o)
    {
        return minutes - o.minutes;
    }
}