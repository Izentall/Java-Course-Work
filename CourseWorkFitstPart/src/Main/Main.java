package Main;

import Service_1.Service1;
import Service_2.Service2;
import Service_3.Service3;

public class Main
{
    public static void main(String[] args)
    {
        Service1 service1 = new Service1("scheduleConfig.json");
        service1.run();
        Service2 service2 = new Service2(service1.getSchedule(),service1.getScheduleConfig());
        service2.run();
        Service3 service3 = new Service3("schedule.json", -7, 7);
        service3.run();
    }
}
