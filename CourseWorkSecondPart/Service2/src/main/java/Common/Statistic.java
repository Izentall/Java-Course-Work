package Common;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Statistic
{
    private static final CargoType[] CARGO_TYPES = {CargoType.POWDERY, CargoType.LIQUID, CargoType.CONTAINER};

    private Map<CargoType, Integer> numbersOfDischargedShipsByType;
    private Map<CargoType, Integer> numbersOfShipsByType;
    private Map<CargoType, Double> averageQueueLengthByType;
    private Map<CargoType, Time> averageWaitingTimeByType;
    private Map<CargoType, Time> maxDelayByType;
    private Map<CargoType, Time> averageDelayByType;
    private Map<CargoType, Integer> fineByType;

    private int numberOfDischargedShips;
    private double averageQueueLength;
    private Time averageWaitingTime;
    private Time maxDelay;
    private Time averageDelay;
    private int fine;

    private TreeSet<Ship> ships;

    public Statistic()
    {
        numbersOfDischargedShipsByType = new HashMap<>();
        numbersOfShipsByType = new HashMap<>();
        averageQueueLengthByType = new HashMap<>();
        averageWaitingTimeByType = new HashMap<>();
        maxDelayByType = new HashMap<>();
        averageDelayByType = new HashMap<>();
        fineByType = new HashMap<>();

        numberOfDischargedShips = 0;
        averageQueueLength = 0.0;
        averageWaitingTime = new Time();
        maxDelay = new Time();
        averageDelay = new Time();
        fine = 0;
    }

    public int getNumberOfDischargedShips() {
        return numberOfDischargedShips;
    }

    public double getAverageQueueLength() {
        return averageQueueLength;
    }

    public Time getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public Time getMaxDelay() {
        return maxDelay;
    }

    public Time getAverageDelay() {
        return averageDelay;
    }

    public int getFine() {
        return fine;
    }

    public int getNumberOfDischargedShips(CargoType cargoType) {
        return numbersOfDischargedShipsByType.get(cargoType);
    }

    public double getAverageQueueLength(CargoType cargoType) {
        return averageQueueLengthByType.get(cargoType);
    }

    public Time getAverageWaitingTime(CargoType cargoType) {
        return averageWaitingTimeByType.get(cargoType);
    }

    public Time getMaxDelay(CargoType cargoType) {
        return maxDelayByType.get(cargoType);
    }

    public Time getAverageDelay(CargoType cargoType) {
        return averageDelayByType.get(cargoType);
    }

    public int getFine(CargoType cargoType) {
        return fineByType.get(cargoType);
    }

    public TreeSet<Ship> getShips()
    {
        return ships;
    }

    private void addNumbersOfDischargedShips(CargoType cargoType, TreeSet<Ship> ships)
    {
        int number = 0;
        int counter = 0;
        for (Ship s:ships)
        {
            ++counter;
            if (s.getCargoType() == cargoType && s.getEndOfDischarge() != null)
                ++number;
        }
        numbersOfDischargedShipsByType.putIfAbsent(cargoType, number);
        numbersOfShipsByType.putIfAbsent(cargoType, counter);
    }

    private void addAverageWaitingTime(CargoType cargoType, TreeSet<Ship> ships)
    {
        int waitingTime = 0;
        for (Ship s:ships)
        {
            if (s.getCargoType() == cargoType)
                waitingTime += (s.getBeginOfDischarge() != null ? s.getBeginOfDischarge().getTime() : 1440*30) - s.getArrival().getTime();
        }
        averageWaitingTimeByType.putIfAbsent(cargoType,
                new Time((numbersOfShipsByType.containsKey(cargoType)
                        && numbersOfShipsByType.get(cargoType) != 0
                        ? waitingTime / numbersOfShipsByType.get(cargoType) : 0)));
    }

    private void addDelayAndFine(CargoType cargoType, TreeSet<Ship> ships)
    {
        int max = 0;
        int counter = 0;
        for (Ship s:ships)
        {
            if (s.getCargoType() == cargoType)
            {
                int temp = (s.getEndOfDischarge() != null ? s.getEndOfDischarge().getTime() : 1440*30) - s.getArrival().getTime() - s.getPlannedWaiting().getTime();
                if (temp > 0)
                    counter += temp;
                if (temp > max)
                    max = temp;
            }
        }
        maxDelayByType.putIfAbsent(cargoType, new Time(max));
        averageDelayByType.putIfAbsent(cargoType,
                new Time((numbersOfShipsByType.containsKey(cargoType)
                        && numbersOfShipsByType.get(cargoType) != 0
                        ? counter / numbersOfShipsByType.get(cargoType) : 0)));
        fineByType.putIfAbsent(cargoType, ((counter / 60) + (counter % 60 == 0 ? 0 : 1)) * 100);
    }

    public void setInfo(TreeSet<Ship> ships, QueueCounter queueCounter)
    {
        this.ships = ships;
        averageQueueLengthByType = queueCounter.getAverageQueueLengthMap();
        for (CargoType c:CARGO_TYPES)
        {
            addNumbersOfDischargedShips(c, ships);
            addAverageWaitingTime(c, ships);
            addDelayAndFine(c, ships);
            numberOfDischargedShips += numbersOfDischargedShipsByType.get(c);
            averageQueueLength += averageQueueLengthByType.get(c);
            averageWaitingTime.setTime(averageWaitingTime.getTime() + averageWaitingTimeByType.get(c).getTime());
            if (maxDelayByType.get(c).getTime() > maxDelay.getTime())
                maxDelay.setTime(maxDelayByType.get(c));
            averageDelay.setTime(averageDelay.getTime() + averageDelayByType.get(c).getTime());
            fine += fineByType.get(c);
        }
        averageQueueLength /= CARGO_TYPES.length;
        averageWaitingTime.setTime(averageWaitingTime.getTime() / CARGO_TYPES.length);
        averageDelay.setTime(averageDelay.getTime() / CARGO_TYPES.length);
    }

    @Override
    public String toString() {
        return  "Number of discharged ships: " + numberOfDischargedShips +
                "; Average queue length: " + averageQueueLength +
                "; Average waiting time: " + averageWaitingTime +
                "; Max delay: " + maxDelay +
                "; Average delay: " + averageDelay +
                "; Fine: " + fine;
    }
}
