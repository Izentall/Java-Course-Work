package Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

public class QueueCounter implements Runnable
{
    private class Pair
    {
        public int queueLength;
        public Time time;

        public Pair()
        {
            queueLength = 0;
            time = new Time();
        }

        public Pair(int queueLength, Time time)
        {
            this.queueLength = queueLength;
            this.time = time;
        }
    }

    private ArrayList<Pair> listPowdery;
    private ArrayList<Pair> listLiquid;
    private ArrayList<Pair> listContainer;
    private AtomicReference<LinkedList<Ship>> queuePowdery;
    private AtomicReference<LinkedList<Ship>> queueLiquid;
    private AtomicReference<LinkedList<Ship>> queueContainer;
    private AtomicReference<Timer> timer;
    private CyclicBarrier barrier;


    public QueueCounter(LinkedList<Ship> queuePowdery,LinkedList<Ship> queueLiquid, LinkedList<Ship> queueContainer,
                        Timer timer, CyclicBarrier barrier)
    {
        this.queuePowdery = new AtomicReference<>(queuePowdery);
        this.queueLiquid = new AtomicReference<>(queueLiquid);
        this.queueContainer = new AtomicReference<>(queueContainer);
        this.timer = new AtomicReference<>(timer);
        this.barrier = barrier;
        this.listPowdery = new ArrayList<>();
        this.listLiquid = new ArrayList<>();
        this.listContainer = new ArrayList<>();
    }

    public Map<CargoType, Double> getAverageQueueLengthMap()
    {
        Map<CargoType, Double> map = new HashMap<>();

        double valuePowdery = 0;
        for (Pair p:listPowdery)
        {
            valuePowdery += (double)(p.time.getTime() * p.queueLength);
        }
        map.putIfAbsent(CargoType.POWDERY, valuePowdery / timer.get().getTime());

        double valueLiquid = 0;
        for (Pair p:listLiquid)
        {
            valueLiquid += (double)(p.time.getTime() * p.queueLength);
        }
        map.putIfAbsent(CargoType.LIQUID, valueLiquid / timer.get().getTime());

        double valueContainer = 0;
        for (Pair p:listContainer)
        {
            valueContainer += (double)(p.time.getTime() * p.queueLength);
        }
        map.putIfAbsent(CargoType.CONTAINER, valueContainer / timer.get().getTime());

        return map;
    }

    @Override
    public void run()
    {
        int lastPowderyMinutes = 0;
        int lastLiquidMinutes = 0;
        int lastContainerMinutes = 0;
        int powderyCounter = 0;
        int liquidCounter = 0;
        int containerCounter = 0;
        int temp = 0;
        while (timer.get().getTime() < timer.get().getDuration())
        {
            try
            {
                synchronized (queuePowdery.get())
                {
                    if (!queuePowdery.get().isEmpty())
                    {
                        temp = 0;
                        for (int i = 0; (i < queuePowdery.get().size()) &&
                                (queuePowdery.get().get(i).getArrival().getTime() < timer.get().getTime()); ++i)
                        {
                            if (queuePowdery.get().get(i).getBeginOfDischarge() == null)
                            {
                                ++temp;
                            }
                        }
                        if (temp != powderyCounter)
                        {
                            listPowdery.add(new Pair(powderyCounter, new Time(timer.get().getTime() - lastPowderyMinutes)));
                            powderyCounter = temp;
                            lastPowderyMinutes = timer.get().getTime();
                        }
                    }
                }
                synchronized (queueLiquid.get())
                {
                    if (!queueLiquid.get().isEmpty())
                    {
                        temp = 0;
                        for (int i = 0; (i < queueLiquid.get().size()) &&
                                (queueLiquid.get().get(i).getArrival().getTime() < timer.get().getTime()); ++i)
                        {
                            if (queueLiquid.get().get(i).getBeginOfDischarge() == null)
                            {
                                ++temp;
                            }
                        }
                        if (temp != liquidCounter)
                        {
                            listLiquid.add(new Pair(liquidCounter, new Time(timer.get().getTime() - lastLiquidMinutes)));
                            liquidCounter = temp;
                            lastLiquidMinutes = timer.get().getTime();
                        }
                    }
                }
                synchronized (queueContainer.get())
                {
                    if (!queueContainer.get().isEmpty())
                    {
                        temp = 0;
                        for (int i = 0; (i < queueContainer.get().size()) &&
                                (queueContainer.get().get(i).getArrival().getTime() < timer.get().getTime()); ++i)
                        {
                            if (queueContainer.get().get(i).getBeginOfDischarge() == null)
                            {
                                ++temp;
                            }
                        }
                        if (temp != containerCounter)
                        {
                            listContainer.add(new Pair(containerCounter, new Time(timer.get().getTime() - lastContainerMinutes)));
                            containerCounter = temp;
                            lastContainerMinutes = timer.get().getTime();
                        }
                    }
                }
                barrier.await();
            }
            catch (InterruptedException | BrokenBarrierException e)
            {
                e.printStackTrace();
            }
        }
    }
}
