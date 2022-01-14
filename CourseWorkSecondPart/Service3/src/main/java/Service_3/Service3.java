package Service_3;

import Common.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class Service3 implements Runnable
{
    private Schedule schedule;
    private LinkedList<Ship> powderyQueue;
    private LinkedList<Ship> liquidQueue;
    private LinkedList<Ship> containerQueue;

    public Service3(Schedule schedule, int lowerBorder, int upperBorder)
    {
        this.schedule = modifySchedule(schedule,lowerBorder,upperBorder);
        this.powderyQueue = getQueue(CargoType.POWDERY);
        this.liquidQueue = getQueue(CargoType.LIQUID);
        this.containerQueue = getQueue(CargoType.CONTAINER);
    }

    public Service3(String jsonFilePath, int lowerBorder, int upperBorder) throws FileNotFoundException
    {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.build();
        Schedule schedule = restTemplate.getForObject("http://localhost:8081/schedule/" + jsonFilePath, Schedule.class);
        if (schedule == null)
        {
            throw new FileNotFoundException("Can not find file");
        }
        this.schedule = modifySchedule(schedule,lowerBorder,upperBorder);
        this.powderyQueue = getQueue(CargoType.POWDERY);
        this.liquidQueue = getQueue(CargoType.LIQUID);
        this.containerQueue = getQueue(CargoType.CONTAINER);
    }

    public Service3(int lowerBorder, int upperBorder)
    {
        try
        {
            RestTemplateBuilder builder = new RestTemplateBuilder();
            RestTemplate restTemplate = builder.build();
            Schedule schedule = restTemplate.getForObject("http://localhost:8081/schedule", Schedule.class);
            if (schedule == null)
            {
                throw new IllegalAccessException("Can not get schedule");
            }
            this.schedule = modifySchedule(schedule,lowerBorder,upperBorder);
            this.powderyQueue = getQueue(CargoType.POWDERY);
            this.liquidQueue = getQueue(CargoType.LIQUID);
            this.containerQueue = getQueue(CargoType.CONTAINER);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public Schedule getSchedule()
    {
        return schedule;
    }

    private Schedule modifySchedule(Schedule schedule, int lowerBorder, int upperBorder)
    {
        lowerBorder *= 1440;
        upperBorder *= 1440;
        Schedule result = new Schedule();
        Random random = new Random();
        for (Note note: schedule.set)
        {
            Note newNote = new Note(note);
            newNote.setPlannedArrival(new Time(newNote.getPlannedArrival().getTime()
                    + random.nextInt(upperBorder - lowerBorder + 1) + lowerBorder));
            result.set.add(newNote);
        }
        return result;
    }

    private LinkedList<Ship> getQueue(CargoType cargoType)
    {
        LinkedList<Ship> queue = new LinkedList<>();
        schedule.set.forEach((Note note)->
        {
            if (note.getCargoType() == cargoType && note.getPlannedArrival().getTime() < 1440 * 30)
            {
                queue.add(new Ship(note));
            }
        });
        return queue;
    }

    private Statistic model(ScheduleConfig scheduleConfig, int numberOfCranesPowdery, int numberOfCranesLiquid, int numberOfCranesContainer)
    {
        System.out.println("Modelling wth cranes " + numberOfCranesPowdery + " " + numberOfCranesLiquid + " " + numberOfCranesContainer);
        TreeSet<Ship> ships = new TreeSet<>();

        Timer timer = new Timer(30 * 1440);
        CyclicBarrier barrier = new CyclicBarrier(numberOfCranesContainer + numberOfCranesPowdery + numberOfCranesLiquid + 1, timer);

        ArrayList<Crane> cranesPowdery = new ArrayList<>(numberOfCranesPowdery);
        ArrayList<Pier> piersPowdery = new ArrayList<>(numberOfCranesPowdery);
        LinkedList<Ship> currentPowderyQueue = new LinkedList<>();
        for (Ship s : powderyQueue) {
            currentPowderyQueue.add(new Ship(s));
        }
        for (int i = 0; i < numberOfCranesPowdery; ++i) {
            cranesPowdery.add(new Crane(scheduleConfig.powderyProductivity, CargoType.POWDERY,
                    timer, barrier, currentPowderyQueue, piersPowdery, ships));
            piersPowdery.add(new Pier(CargoType.POWDERY));
        }

        ArrayList<Crane> cranesLiquid = new ArrayList<>(numberOfCranesLiquid);
        ArrayList<Pier> piersLiquid = new ArrayList<>(numberOfCranesLiquid);
        LinkedList<Ship> currentLiquidQueue = new LinkedList<>();
        for (Ship s : liquidQueue) {
            currentLiquidQueue.add(new Ship(s));
        }
        for (int i = 0; i < numberOfCranesLiquid; ++i) {
            cranesLiquid.add(new Crane(scheduleConfig.liquidProductivity, CargoType.LIQUID,
                    timer, barrier, currentLiquidQueue, piersLiquid, ships));
            piersLiquid.add(new Pier(CargoType.LIQUID));
        }

        ArrayList<Crane> cranesContainer = new ArrayList<>(numberOfCranesContainer);
        ArrayList<Pier> piersContainer = new ArrayList<>(numberOfCranesContainer);
        LinkedList<Ship> currentContainerQueue = new LinkedList<>();
        for (Ship s : containerQueue) {
            currentContainerQueue.add(new Ship(s));
        }
        for (int i = 0; i < numberOfCranesContainer; ++i) {
            cranesContainer.add(new Crane(scheduleConfig.containerProductivity, CargoType.CONTAINER,
                    timer, barrier, currentContainerQueue, piersContainer, ships));
            piersContainer.add(new Pier(CargoType.CONTAINER));
        }

        QueueCounter queueCounter = new QueueCounter(currentPowderyQueue, currentLiquidQueue, currentContainerQueue, timer, barrier);

        ArrayList<Thread> threads = new ArrayList<>(numberOfCranesContainer + numberOfCranesPowdery + numberOfCranesLiquid + 1);
        for (Crane c : cranesPowdery) {
            Thread thread = new Thread(c);
            threads.add(thread);
            thread.start();
        }
        for (Crane c : cranesLiquid) {
            Thread thread = new Thread(c);
            threads.add(thread);
            thread.start();
        }
        for (Crane c : cranesContainer) {
            Thread thread = new Thread(c);
            threads.add(thread);
            thread.start();
        }
        threads.add(new Thread(queueCounter));
        threads.get(threads.size() - 1).start();

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Pier p : piersPowdery) {
            if (!p.isFree()) {
                ships.add(p.getShip());
            }
        }
        for (Pier p : piersLiquid) {
            if (!p.isFree()) {
                ships.add(p.getShip());
            }
        }
        for (Pier p : piersContainer) {
            if (!p.isFree()) {
                ships.add(p.getShip());
            }
        }

        ships.addAll(currentPowderyQueue);
        ships.addAll(currentLiquidQueue);
        ships.addAll(currentContainerQueue);

        Statistic statistic = new Statistic();
        statistic.setInfo(ships, queueCounter);

        return statistic;
    }

    @Override
    public void run()
    {
        System.out.println("Modified schedule");
        schedule.set.forEach(System.out::println);

        try
        {
            RestTemplateBuilder builder = new RestTemplateBuilder();
            RestTemplate restTemplate = builder.build();
            ScheduleConfig scheduleConfig = restTemplate.getForObject("http://localhost:8080/schedule_config/", ScheduleConfig.class);
            if (scheduleConfig == null)
            {
                throw new IllegalAccessException("Can not get schedule config");
            }
            int numberOfCranesPowdery = 1;
            int numberOfCranesLiquid = 1;
            int numberOfCranesContainer = 1;
            Statistic statistic = model(scheduleConfig, numberOfCranesPowdery, numberOfCranesLiquid, numberOfCranesContainer);
            int finePowdery = statistic.getFine(CargoType.POWDERY);
            int fineLiquid = statistic.getFine(CargoType.LIQUID);
            int fineContainer = statistic.getFine(CargoType.CONTAINER);
            int totalPowderyCargo = 0;
            for (Ship s:powderyQueue)
            {
                if (s.getArrival().getTime() < 30 * 1440)
                {
                    totalPowderyCargo += s.getCargoValue().get();
                }
            }
            int totalLiquidCargo = 0;
            for (Ship s:liquidQueue)
            {
                if (s.getArrival().getTime() < 30 * 1440)
                {
                    totalLiquidCargo += s.getCargoValue().get();
                }
            }
            int totalContainerCargo = 0;
            for (Ship s:containerQueue)
            {
                if (s.getArrival().getTime() < 30 * 1440)
                {
                    totalContainerCargo += s.getCargoValue().get();
                }
            }
            boolean p = false;
            boolean l = false;
            boolean c = false;
            int approximate = 10;
            while (finePowdery > Crane.cranePrice || fineLiquid > Crane.cranePrice || fineContainer > Crane.cranePrice)
            {

                if (finePowdery > Crane.cranePrice)
                {
                    if (!p && finePowdery > Crane.cranePrice * approximate)
                    {
                        numberOfCranesPowdery = totalPowderyCargo / (scheduleConfig.powderyProductivity * 60);
                        p = true;
                    }
                    else
                    {
                        numberOfCranesPowdery += finePowdery / Crane.cranePrice;
                    }
                }
                if (fineLiquid > Crane.cranePrice)
                {
                    if (!l && fineLiquid > Crane.cranePrice * approximate)
                    {
                        numberOfCranesLiquid = totalLiquidCargo / (scheduleConfig.liquidProductivity * 60);
                        l = true;
                    }
                    else
                    {
                        numberOfCranesLiquid += fineLiquid / Crane.cranePrice;
                    }
                }
                if (fineContainer > Crane.cranePrice)
                {
                    if (!c && fineContainer > Crane.cranePrice * approximate)
                    {
                        numberOfCranesContainer += totalContainerCargo / (scheduleConfig.containerProductivity * 60);
                        c = true;
                    }
                    else
                    {
                        numberOfCranesContainer += fineContainer / Crane.cranePrice;
                    }
                }
                statistic = model(scheduleConfig, numberOfCranesPowdery, numberOfCranesLiquid, numberOfCranesContainer);
                finePowdery = statistic.getFine(CargoType.POWDERY);
                fineLiquid = statistic.getFine(CargoType.LIQUID);
                fineContainer = statistic.getFine(CargoType.CONTAINER);
            }
            Report report = new Report(statistic, numberOfCranesPowdery, numberOfCranesLiquid, numberOfCranesContainer);
            builder = new RestTemplateBuilder();
            restTemplate = builder.build();
            restTemplate.postForObject("http://localhost:8081/report", report, Report.class);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
