package Common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.TreeSet;

@JsonAutoDetect
public class Report
{
    public TreeSet<Ship> ships;
    public int numberOfDischargedShips;
    public double averageQueueLength;
    public Time averageWaitingTime;
    public Time maxDelay;
    public Time averageDelay;
    public int fine;
    public int numberOfCranesPowdery;
    public int numberOfCranesLiquid;
    public int numberOfCranesContainer;
    public int totalExpenses;

    public Report()
    {
        ships = new TreeSet<>();
        numberOfDischargedShips = 0;
        averageQueueLength = 0;
        averageWaitingTime = new Time();
        maxDelay = new Time();
        averageDelay = new Time();
        fine = 0;
        numberOfCranesPowdery = 0;
        numberOfCranesLiquid = 0;
        numberOfCranesContainer = 0;
        totalExpenses = 0;
    }

    public Report(Statistic statistic, int numberOfCranesPowdery, int numberOfCranesLiquid, int numberOfCranesContainer, int cranePrice)
    {
        ships = new TreeSet<>(statistic.getShips());
        numberOfDischargedShips = statistic.getNumberOfDischargedShips();
        averageQueueLength = statistic.getAverageQueueLength();
        averageWaitingTime = new Time(statistic.getAverageWaitingTime());
        maxDelay = new Time(statistic.getMaxDelay());
        averageDelay = new Time(statistic.getAverageDelay());
        fine = statistic.getFine();
        this.numberOfCranesPowdery = numberOfCranesPowdery;
        this.numberOfCranesLiquid = numberOfCranesLiquid;
        this.numberOfCranesContainer = numberOfCranesContainer;
        totalExpenses = (numberOfCranesPowdery + numberOfCranesLiquid + numberOfCranesContainer - 3)
                * cranePrice + fine;
    }
}
