package Service_1;

import Common.Schedule;
import Common.ScheduleConfig;

public interface Service1
{
    public void renewSchedule();
    public Schedule getSchedule();
    public void setSchedule(Schedule schedule);
    public ScheduleConfig getScheduleConfig();
    public void setScheduleConfig(ScheduleConfig scheduleConfig);
}
