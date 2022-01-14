package Service_3;

import Common.CargoType;

import java.util.concurrent.atomic.AtomicInteger;

public class Pier
{
    private Ship ship;
    private AtomicInteger craneCounter;
    private AtomicInteger dischargedCargo;
    private CargoType cargoType;

    public Pier(CargoType cargoType)
    {
        ship = null;
        craneCounter = new AtomicInteger(0);
        dischargedCargo = new AtomicInteger(0);
        this.cargoType = cargoType;
    }

    public void moor(Ship ship)
    {
        this.ship = ship;
    }

    public Ship getShip()
    {
        return ship;
    }

    public int getCraneCounter()
    {
        return craneCounter.get();
    }

    public int getDischargedCargo()
    {
        return dischargedCargo.get();
    }

    public CargoType getCargoType()
    {
        return cargoType;
    }

    public int discharge(int cargoValue)
    {
        return dischargedCargo.addAndGet(cargoValue);
    }

    public boolean isFree()
    {
        return ship == null;
    }

    public int addCrane()
    {
        return craneCounter.addAndGet(1);
    }

    public int freeCrane()
    {
        return craneCounter.addAndGet(-1);
    }

    public Ship free()
    {
        Ship temp = ship;
        ship = null;
        craneCounter.set(0);
        dischargedCargo.set(0);
        return temp;
    }

    public boolean isDischarged()
    {
        return ship.getCargoValue().get() <= dischargedCargo.get();
    }
}
