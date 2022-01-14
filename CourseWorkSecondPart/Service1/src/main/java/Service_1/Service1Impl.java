package Service_1;

import Common.Note;
import Common.Schedule;
import Common.ScheduleConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Scanner;

@Service
public class Service1Impl implements Runnable, Service1
{
    private ScheduleConfig scheduleConfig;
    private Schedule schedule;

    public Service1Impl()
    {
        try
        {
            Scanner sc = new Scanner(new File("src\\main\\resources\\scheduleConfig.json"));
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
            System.out.println("Service 1 created");
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

    @Override
    public void renewSchedule()
    {
        ScheduleGenerator scheduleGenerator = new ScheduleGenerator(scheduleConfig);
        schedule = scheduleGenerator.generate();
    }

    @Override
    public Schedule getSchedule()
    {
        return schedule;
    }

    @Override
    public void setSchedule(Schedule schedule)
    {
        this.schedule = schedule;
    }

    @Override
    public ScheduleConfig getScheduleConfig()
    {
        return scheduleConfig;
    }

    @Override
    public void setScheduleConfig(ScheduleConfig scheduleConfig)
    {
        this.scheduleConfig = scheduleConfig;
    }
}
