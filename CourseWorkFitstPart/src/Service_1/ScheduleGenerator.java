package Service_1;

import Common.*;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class ScheduleGenerator
{
    private ScheduleConfig scheduleConfig;
    private Random randomGenerator;
    Set<String> names;

    public ScheduleGenerator(ScheduleConfig scheduleConfig)
    {
        this.scheduleConfig = scheduleConfig;
        randomGenerator = new Random();
        names = new TreeSet<>();
    }

    private String generateName()
    {
        int length = 20;
        StringBuilder str = new StringBuilder(length);
        for (int i = 0;i < length; ++i)
        {
            str.append((char)(65 + randomGenerator.nextInt(26) + 32 * randomGenerator.nextInt(2)));
        }
        return new String(str);
    }

    private Note generateNote()
    {
        String name = generateName();
        while (names.contains(name))
        {
            name = generateName();
        }
        CargoType cargoType;
        int cargoValue;
        int waitingTimeMinutes = 0;

        switch (randomGenerator.nextInt(3))
        {
            case 0 ->
                    {
                        cargoType = CargoType.POWDERY;
                        cargoValue = randomGenerator.nextInt(scheduleConfig.maxWeightPowdery - scheduleConfig.minWeightPowdery + 1)
                                + scheduleConfig.minWeightPowdery;
                        waitingTimeMinutes = (cargoValue / scheduleConfig.powderyProductivity +
                                ((cargoValue % scheduleConfig.powderyProductivity) == 0 ? 0 : 1)) * 60 + 1440;
                    }
            case 1 ->
                    {
                        cargoType = CargoType.LIQUID;
                        cargoValue = randomGenerator.nextInt(scheduleConfig.maxWeightLiquid - scheduleConfig.minWeightLiquid + 1)
                                + scheduleConfig.minWeightLiquid;
                        waitingTimeMinutes = (cargoValue / scheduleConfig.liquidProductivity +
                                ((cargoValue % scheduleConfig.liquidProductivity) == 0 ? 0 : 1)) * 60 + 1440;
                    }
            default ->
                    {
                        cargoType = CargoType.CONTAINER;
                        cargoValue = randomGenerator.nextInt(scheduleConfig.maxNumberOfContainers - scheduleConfig.minNumberOfContainers + 1)
                                + scheduleConfig.minNumberOfContainers;
                        waitingTimeMinutes = (cargoValue / scheduleConfig.containerProductivity +
                                ((cargoValue % scheduleConfig.containerProductivity) == 0 ? 0 : 1)) * 60 + 1440;
                    }
        }

        int lastTimeMinutes = 1440 * 30;
        Time arrival = new Time(randomGenerator.nextInt(lastTimeMinutes + 1));
        Time waiting = new Time(waitingTimeMinutes);
        return new Note(name, cargoType, cargoValue, arrival, waiting);
    }

    public Schedule generate()
    {
        names.clear();
        TreeSet<Note> set = new TreeSet<>();
        int size = randomGenerator.nextInt(scheduleConfig.upperSize - scheduleConfig.lowerSize + 1)
                + scheduleConfig.lowerSize;
        for (int i = 0; i < size; ++i)
        {
            set.add(generateNote());
        }
        return new Schedule(set);
    }
}