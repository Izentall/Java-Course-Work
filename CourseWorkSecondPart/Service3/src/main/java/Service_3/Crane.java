package Service_3;

import Common.CargoType;
import Common.Time;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

public class Crane implements Runnable
{
    public static final int cranePrice = 30000;

    private int productivity;
    private CargoType cargoType;
    private AtomicReference<Pier> pier;
    private AtomicReference<Timer> timer;
    private CyclicBarrier barrier;
    private AtomicReference<Queue<Ship>> queue;
    private AtomicReference<ArrayList<Pier>> pierList;
    private AtomicReference<TreeSet<Ship>> shipSet;
    private int repairTime;
    private Random random;

    private static Object mutex = new Object();

    public Crane(int productivity, CargoType cargoType, Timer timer, CyclicBarrier barrier,
                 Queue<Ship> queue, ArrayList<Pier> pierList, TreeSet<Ship> shipSet)
    {
        this.productivity = productivity;
        this.cargoType = cargoType;
        this.pier = null;
        this.timer = new AtomicReference(timer);
        this.barrier = barrier;
        this.queue = new AtomicReference(queue);
        this.pierList = new AtomicReference<>(pierList);
        this.shipSet = new AtomicReference<>(shipSet);
        this.repairTime = 0;
        this.random = new Random();
    }

    private AtomicReference<Pier> getNearestPier()
    {
        AtomicReference<Pier> temp = null;
        for (Pier pier: pierList.get())
        {
            if (temp == null && pier.getCraneCounter() == 1)
                temp = new AtomicReference<>(pier);
            else if (temp != null && (pier.getCraneCounter() == 1) && ((temp.get().getShip().getPlannedWaiting().getTime()
                        + temp.get().getShip().getBeginOfDischarge().getTime() - timer.get().getTime())
                    > (pier.getShip().getPlannedWaiting().getTime()
                        + pier.getShip().getBeginOfDischarge().getTime() - timer.get().getTime())))
            {
                temp.set(pier);
            }
        }
        return temp;
    }

    private AtomicReference<Pier> findFreePier()
    {
        AtomicReference<Pier> pierAtomicReference = null;
        for (Pier p: pierList.get())
        {
            if (p.isFree())
                pierAtomicReference = new AtomicReference<>(p);
            break;
        }
        return pierAtomicReference;
    }

    @Override
    public void run()
    {
        while (timer.get().getTime() < timer.get().getDuration())
        {
            try
            {
                if (repairTime != 0) //wait
                    --repairTime;
                else if (pier == null) //find ship
                {
                    pier = getNearestPier();
                    synchronized (queue.get())
                    {
                        if (!queue.get().isEmpty())
                        {
                            if (queue.get().element().getArrival().getTime() <= timer.get().getTime())
                            {
                                AtomicReference<Pier> pierAtomicReference = findFreePier();

                                if (pierAtomicReference != null && (pier == null || ((queue.get().element().getArrival().getTime()
                                        + queue.get().element().getPlannedWaiting().getTime() - timer.get().getTime())
                                        < pier.get().getShip().getPlannedWaiting().getTime()
                                        + pier.get().getShip().getBeginOfDischarge().getTime() - timer.get().getTime())))
                                {
                                    Ship ship = queue.get().poll();
                                    assert ship != null;
                                    ship.setBeginOfDischarge(new Time(timer.get().getTime()));
                                    pierAtomicReference.get().moor(ship);
                                    pier = pierAtomicReference;
                                    pier.get().addCrane();
                                }
                            }
                        }
                    }
                }
                if (pier != null)//discharge
                {
                    synchronized (pier.get())
                    {
                        if (!pier.get().isFree())
                            pier.get().discharge(productivity / 60);

                        if (!pier.get().isFree() && pier.get().isDischarged()) {
                            pier.get().getShip().setEndOfDischarge(new Time(timer.get().getTime() + 1));
                            shipSet.get().add(pier.get().free());
                        }

                        if (pier.get().isFree()) {
                            pier = null;
                            repairTime = random.nextInt(1441);
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
