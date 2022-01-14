package Common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class ScheduleConfig
{
    public int powderyProductivity;
    public int liquidProductivity;
    public int containerProductivity;
    public int lowerSize;
    public int upperSize;
    public int minWeightPowdery;
    public int maxWeightPowdery;
    public int minWeightLiquid;
    public int maxWeightLiquid;
    public int minNumberOfContainers;
    public int maxNumberOfContainers;

    public ScheduleConfig()
    {
        powderyProductivity = 0;
        liquidProductivity = 0;
        containerProductivity = 0;
        lowerSize = 0;
        upperSize = 0;
        minWeightPowdery = 0;
        maxWeightPowdery = 0;
        minWeightLiquid = 0;
        maxWeightLiquid = 0;
        minNumberOfContainers = 0;
        maxNumberOfContainers = 0;
    }
    public ScheduleConfig(int powderyProductivity,
                          int liquidProductivity,
                          int containerProductivity,
                          int minWeightPowdery,
                          int maxWeightPowdery,
                          int minWeightLiquid,
                          int maxWeightLiquid,
                          int minNumberOfContainers,
                          int maxNumberOfContainers,
                          int lowerSize,
                          int upperSize)
    {
        if (powderyProductivity <= 0 || liquidProductivity <= 0 || containerProductivity <= 0
                || minWeightPowdery <= 0 || maxWeightPowdery <= 0
                || minWeightLiquid <= 0 || maxWeightLiquid <= 0
                || minNumberOfContainers <= 0 || maxNumberOfContainers <= 0
                || lowerSize <= 0 || upperSize <= 0)
        {
            throw new IllegalArgumentException("Parameters must be positive");
        }
        if (upperSize <= lowerSize)
        {
            throw new IllegalArgumentException("Upper size must be more than lower size");
        }
        if (maxWeightPowdery < minWeightPowdery)
        {
            throw new IllegalArgumentException("Max weight powdery must be more than min or equal");
        }
        if (maxWeightLiquid < minWeightLiquid)
        {
            throw new IllegalArgumentException("Max weight liquid must be more than min or equal");
        }
        if (maxNumberOfContainers < minNumberOfContainers)
        {
            throw new IllegalArgumentException("Max number of containers must be more than min or equal");
        }
        this.powderyProductivity = powderyProductivity;
        this.liquidProductivity = liquidProductivity;
        this.containerProductivity = containerProductivity;
        this.minWeightPowdery = minWeightPowdery;
        this.maxWeightPowdery = maxWeightPowdery;
        this.minWeightLiquid = minWeightLiquid;
        this.maxWeightLiquid = maxWeightLiquid;
        this.minNumberOfContainers = minNumberOfContainers;
        this.maxNumberOfContainers = maxNumberOfContainers;
        this.lowerSize = lowerSize;
        this.upperSize = upperSize;
    }

}
