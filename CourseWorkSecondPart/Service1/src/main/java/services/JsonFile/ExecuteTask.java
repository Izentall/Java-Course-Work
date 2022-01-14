package services.JsonFile;

import Common.Schedule;
import Common.ScheduleConfig;
import Service_1.Service1;
import Service_1.Service1Impl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class ExecuteTask {

    private final static Service1 service1 = new Service1Impl();

    @GetMapping(value = "/schedule")
    public ResponseEntity<Schedule> getSchedule()
    {
        Schedule schedule = service1.getSchedule();
        return schedule != null
                ? new ResponseEntity<>(schedule, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(value = "/schedule_config")
    public ResponseEntity<ScheduleConfig> getScheduleConfig()
    {
        return service1.getSchedule() != null
                ? new ResponseEntity<>(service1.getScheduleConfig(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping(value = "/schedule/renew")
    public ResponseEntity<?> renew()
    {
        service1.renewSchedule();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
