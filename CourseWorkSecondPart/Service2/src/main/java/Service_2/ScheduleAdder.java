package Service_2;

import Common.*;

public class ScheduleAdder
{
    public static void add(Schedule schedule, ScheduleConfig config, String name,
                           CargoType cargoType, int cargoValue, Time plannedArrival)
    {
        int waitingTimeMinutes = 0;
        switch (cargoType)
        {
            case POWDERY -> waitingTimeMinutes = (cargoValue / config.powderyProductivity +
                    ((cargoValue % config.powderyProductivity) == 0 ? 0 : 1)) * 60 + 1440;
            case LIQUID -> waitingTimeMinutes = (cargoValue / config.liquidProductivity +
                    ((cargoValue % config.liquidProductivity) == 0 ? 0 : 1)) * 60 + 1440;
            case CONTAINER -> waitingTimeMinutes = (cargoValue / config.containerProductivity +
                    ((cargoValue % config.containerProductivity) == 0 ? 0 : 1)) * 60 + 1440;
        }
        Note added = new Note(name, cargoType, cargoValue, plannedArrival, new Time(waitingTimeMinutes));
        schedule.set.add(added);
    }

    public static CargoType parseCargoType(String str) throws IllegalArgumentException
    {
        if (str.equalsIgnoreCase("powdery"))
        {
            return CargoType.POWDERY;
        }
        if (str.equalsIgnoreCase("liquid"))
        {
            return CargoType.LIQUID;
        }
        if (str.equalsIgnoreCase("container"))
        {
            return CargoType.CONTAINER;
        }
        throw new IllegalArgumentException("Illegal cargo type");
    }
}
