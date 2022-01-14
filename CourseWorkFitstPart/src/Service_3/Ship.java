package Service_3;

import Common.CargoType;
import Common.Note;
import Common.Time;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class Ship implements Comparable<Ship>
{
    private String name;
    private CargoType cargoType;
    private AtomicInteger cargoValue;
    private Time arrival;
    private Time plannedWaiting;
    private Time beginOfDischarge;
    private Time endOfDischarge;

    public String getName() {
        return name;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public AtomicInteger getCargoValue() {
        return cargoValue;
    }

    public Time getArrival() {
        return arrival;
    }

    public Time getPlannedWaiting() {
        return plannedWaiting;
    }

    public Time getBeginOfDischarge() {
        return beginOfDischarge;
    }

    public Time getEndOfDischarge() {
        return endOfDischarge;
    }

    public void setBeginOfDischarge(Time beginOfDischarge)
    {
        this.beginOfDischarge = beginOfDischarge;
    }

    public void setEndOfDischarge(Time endOfDischarge)
    {
        this.endOfDischarge = endOfDischarge;
    }

    public Ship(Note note)
    {
        this.name = note.getName();
        this.cargoType = note.getCargoType();
        this.cargoValue = new AtomicInteger(note.getCargoValue());
        this.arrival = note.getPlannedArrival();
        this.plannedWaiting = note.getPlannedWaiting();
        this.beginOfDischarge = null;
        this.endOfDischarge = null;
    }

    public Ship(Ship ship)
    {
        this.name = ship.name;
        this.cargoType = ship.cargoType;
        this.cargoValue = new AtomicInteger(ship.cargoValue.get());
        this.arrival = new Time(ship.arrival);
        this.plannedWaiting = new Time(ship.plannedWaiting);
        this.beginOfDischarge = (ship.beginOfDischarge == null ? null : new Time(ship.beginOfDischarge));
        this.endOfDischarge = (ship.endOfDischarge == null ? null : new Time(ship.endOfDischarge));
    }

    public String getInfo()
    {
        StringBuilder result = new StringBuilder();
        result.append("Name: " + name + "; ");
        result.append("Arrival: " + arrival.toString() + "; ");
        result.append("Waiting for discharge: " + new Time((beginOfDischarge != null ? beginOfDischarge.getTime() : 1440 * 30) - arrival.getTime()).toString() + "; ");
        result.append("Begin of discharge: " + (beginOfDischarge != null ? beginOfDischarge.toString() : " was not begun") + "; ");
        result.append("Duration of discharge: " + (beginOfDischarge != null ? (endOfDischarge != null ? new Time(endOfDischarge.getTime() - beginOfDischarge.getTime()) : " was not ended") : " was not begun"));
        return result.toString();
    }

    @Override
    public int compareTo(@NotNull Ship o)
    {
        return arrival.compareTo(o.arrival);
    }

    @Override
    public String toString() {
        return "Ship{" +
                "name='" + name + '\'' +
                ", cargoType=" + cargoType +
                ", cargoValue=" + cargoValue +
                ", plannedArrival=" + arrival +
                ", plannedWaiting=" + plannedWaiting +
                ", beginOfDischarge=" + beginOfDischarge +
                ", endOfDischarge=" + endOfDischarge +
                '}';
    }
}
