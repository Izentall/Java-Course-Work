package Service_2;

import Common.Report;
import Common.Schedule;
import Common.ScheduleConfig;
import Common.Statistic;

public interface Service2
{
    public Schedule getSchedule();
    public Schedule getSchedule(String path);
    public boolean saveResults(Report report);
}
