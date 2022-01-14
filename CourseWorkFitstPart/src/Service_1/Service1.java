package Service_1;

import Common.Note;
import Common.Schedule;
import Common.ScheduleConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Scanner;

public class Service1 implements Runnable
{
    public static void main(String[] args)
    {
        Service1 service1 = new Service1("scheduleConfig.json");
        service1.run();
    }

    private ScheduleConfig scheduleConfig;
    private Schedule schedule;

    public Service1(String configPath)
    {
        try
        {
            Scanner sc = new Scanner(new File(configPath));
            StringBuilder jsonObj = new StringBuilder();
            while (sc.hasNext())
            {
                jsonObj.append(sc.nextLine());
            }
            StringReader reader = new StringReader(jsonObj.toString());
            ObjectMapper mapper = new ObjectMapper();
            scheduleConfig = mapper.readValue(reader, ScheduleConfig.class);
            ScheduleGenerator scheduleGenerator = new ScheduleGenerator(scheduleConfig);
            schedule = scheduleGenerator.generate();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        for (Note i : schedule.set)
        {
            System.out.println(i.toString());
        }
    }

    public Schedule getSchedule()
    {
        return schedule;
    }

    public void setSchedule(Schedule schedule)
    {
        this.schedule = schedule;
    }

    public ScheduleConfig getScheduleConfig()
    {
        return scheduleConfig;
    }

    public void setScheduleConfig(ScheduleConfig scheduleConfig)
    {
        this.scheduleConfig = scheduleConfig;
    }
}
