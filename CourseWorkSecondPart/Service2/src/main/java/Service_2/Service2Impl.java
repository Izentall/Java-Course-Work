package Service_2;

import Common.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Service2Impl implements Runnable, Service2
{
    private Schedule schedule;
    private ScheduleConfig scheduleConfig;

    public Service2Impl()
    {
        schedule = null;
        scheduleConfig = null;
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
        } while (!(answer != null && answer.equalsIgnoreCase("no")));
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

    @Override
    public Schedule getSchedule()
    {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.build();
        schedule = restTemplate.getForObject("http://localhost:8080/schedule", Schedule.class);
        scheduleConfig = restTemplate.getForObject("http://localhost:8080/schedule_config", ScheduleConfig.class);
        addNotes();
        String fileName = "schedule.json";
        try {
            ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));
            mapper.writeValue(writer, schedule);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        return schedule;
    }

    @Override
    public Schedule getSchedule(String fileName)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File(fileName), Schedule.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean saveResults(Report report)
    {
        try {
            ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("results.json"));
            mapper.writeValue(writer, report);
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
