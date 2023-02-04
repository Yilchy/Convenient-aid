package ua.edu.sumdu.volonteerProject.serviceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.volonteerProject.model.City;
import ua.edu.sumdu.volonteerProject.model.LastPollAndSendCityChecker;
import ua.edu.sumdu.volonteerProject.repos.LastPollAndSendCityCheckerRepo;
import ua.edu.sumdu.volonteerProject.services.LastPollAndSecurityCheckerService;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class lastPollAndSecurityCheckerServiceImpl implements LastPollAndSecurityCheckerService {
    private final LastPollAndSendCityCheckerRepo pollAndSendCityCheckerRepo;

    @Override
    public long getLastPollingExpirationDifferance(City city) {
        LastPollAndSendCityChecker lastPollingDateCh = pollAndSendCityCheckerRepo.findLastPollAndSendCityCheckerByCity_Name(city.getName());
        if(lastPollingDateCh==null){
            pollAndSendCityCheckerRepo.saveAndFlush(new LastPollAndSendCityChecker(city.getName() ,
                    city,
                    Timestamp.from(Instant.now().atZone(ZoneOffset.UTC).minus(1,ChronoUnit.DAYS).toInstant()),
                    Timestamp.from(Instant.now().atZone(ZoneOffset.UTC).minus(1,ChronoUnit.DAYS).toInstant())));
            return 10;
        }
        Timestamp lastPollingDate = lastPollingDateCh.getDateOfLastPolling();
        Instant currTimeADayAgo = Instant.now().atZone(ZoneOffset.UTC).minus(1, ChronoUnit.DAYS).toInstant();
        Instant lastPoll = lastPollingDate.toInstant();
        long diff = currTimeADayAgo.getEpochSecond() - lastPoll.getEpochSecond();
        return diff;
    }

    @Override
    public long getLastSendMessageExpirationDifferance(City city) {
        LastPollAndSendCityChecker lastPollingDateCh = pollAndSendCityCheckerRepo.findLastPollAndSendCityCheckerByCity_Name(city.getName());
        if(lastPollingDateCh==null){
            pollAndSendCityCheckerRepo.save(new LastPollAndSendCityChecker(city.getName() ,city, null, null));
            return 10;
        }
        Timestamp lastSendMessageDate = lastPollingDateCh.getDateOfLastSendingLocation();
        Instant currTimeADayAgo = Instant.now().atZone(ZoneOffset.UTC).minus(1, ChronoUnit.DAYS).toInstant();
        Instant lastPoll = lastSendMessageDate.toInstant();
        long diff = currTimeADayAgo.getEpochSecond() - lastPoll.getEpochSecond();
        return diff;
    }
}
