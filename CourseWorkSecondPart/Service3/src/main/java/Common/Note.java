package Common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class Note implements Comparable<Note>
{
    private String name;
    private CargoType cargoType;
    private int cargoValue;
    private Time plannedArrival;
    private Time plannedWaiting;

    public Note()
    {
        name = null;
        cargoType = CargoType.POWDERY;
        cargoValue = 0;
        plannedArrival = new Time();
        plannedWaiting = new Time();
    }
    public Note(Note note)
    {
        this.name = note.name;
        this.cargoType = note.cargoType;
        this.cargoValue = note.cargoValue;
        this.plannedArrival = note.plannedArrival;
        this.plannedWaiting = note.plannedWaiting;
    }

    public Note(String name, CargoType cargoType, int cargoValue, Time plannedArrival, Time plannedWaiting)
    {
        if (name == null || cargoType == null || plannedArrival == null || plannedWaiting == null)
        {
            throw new IllegalArgumentException("Note argument is null");
        }
        this.name = name;
        this.cargoType = cargoType;
        this.cargoValue = cargoValue;
        this.plannedArrival = plannedArrival;
        this.plannedWaiting = plannedWaiting;
    }

    public String getName()
    {
        return name;
    }

    public CargoType getCargoType()
    {
        return cargoType;
    }

    public int getCargoValue()
    {
        return cargoValue;
    }

    public Time getPlannedArrival()
    {
        return plannedArrival;
    }

    public Time getPlannedWaiting()
    {
        return plannedWaiting;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setCargoType(CargoType cargoType)
    {
        this.cargoType = cargoType;
    }

    public void setCargoValue(int cargoValue)
    {
        this.cargoValue = cargoValue;
    }

    public void setPlannedArrival(Time plannedArrival)
    {
        this.plannedArrival = plannedArrival;
    }

    public void setPlannedWaiting(Time plannedWaiting)
    {
        this.plannedWaiting = plannedWaiting;
    }

    @Override
    public int compareTo(Note o)
    {
        return plannedArrival.compareTo(o.plannedArrival);
    }

    @Override
    public String toString() {
        return "Note{" +
                "name='" + name + '\'' +
                ", cargoType=" + cargoType.toString() +
                ", cargoValue=" + cargoValue +
                ", plannedArrival=" + plannedArrival.toString() +
                ", plannedWaiting=" + plannedWaiting.toString() +
                '}';
    }
}
