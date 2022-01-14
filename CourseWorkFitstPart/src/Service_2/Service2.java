package Service_2;

import Common.CargoType;
import Common.Schedule;
import Common.ScheduleConfig;
import Common.Time;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Service2 implements Runnable
{
    private Schedule schedule;
    private ScheduleConfig scheduleConfig;

    public Service2(Schedule schedule, ScheduleConfig scheduleConfig)
    {
        this.schedule = schedule;
        this.scheduleConfig = scheduleConfig;
    }

    private void addNotes()
    {
        String answer = null;
        Reader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        do
        {
            try
            {
                System.out.println("Do you want to add a note to schedule? (yes/no)");
                answer = bufferedReader.readLine();

                if (answer.equalsIgnoreCase("yes"))
                {
                    System.out.println("Enter name:");
                    String name = bufferedReader.readLine();
                    System.out.println("Enter cargo type (POWDERY/LIQUID/CONTAINER):");
                    String cargo = bufferedReader.readLine();
                    CargoType cargoType = ScheduleAdder.parseCargoType(cargo);
                    System.out.println("Enter cargo value:");
                    int cargoValue = Integer.parseInt(bufferedReader.readLine());
                    if (cargoValue < 1)
                    {
                        throw new IllegalArgumentException("Cargo value must be positive");
                    }
                    System.out.println("Enter planned arrival time (dd:hh:mm):");
                    String time = bufferedReader.readLine();
                    Time plannedArrival = Time.parseTime(time);
                    ScheduleAdder.add(schedule, scheduleConfig, name, cargoType, cargoValue, plannedArrival);
                    System.out.println("Ship is added");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        } while (!(answer != null ? answer.equalsIgnoreCase("no") : false));
    }

    @Override
    public void run()
    {
        addNotes();
        try {
            ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("schedule.json"));
            mapper.writeValue(writer, schedule);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
