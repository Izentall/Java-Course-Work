package services.JsonFile;

import Common.Report;
import Common.Schedule;
import Common.ScheduleConfig;
import Service_2.Service2;
import Service_2.Service2Impl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@SpringBootApplication
@RestController
public class ExecuteTask {

    private final static Service2 service2 = new Service2Impl();

    @GetMapping(value = "/schedule")
    public ResponseEntity<Schedule> getSchedule()
    {
        Schedule schedule = service2.getSchedule();
        return schedule != null
                ? new ResponseEntity<>(schedule, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(value = "/schedule/{fileName}")
    public ResponseEntity<Schedule> getSchedule(@PathVariable("fileName") String fileName)
    {
        Schedule schedule = service2.getSchedule(fileName);
        return schedule != null
                ? new ResponseEntity<>(schedule, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/report")
    public ResponseEntity<?> saveResults(@RequestBody Report report)
    {
        return service2.saveResults(report)
                ? new ResponseEntity<>(HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
