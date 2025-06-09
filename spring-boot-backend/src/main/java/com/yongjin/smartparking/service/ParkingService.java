package com.yongjin.smartparking.service;

import com.yongjin.smartparking.model.ParkingRecord;
import com.yongjin.smartparking.repository.ParkingRecordRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ParkingService {
    private final ParkingRecordRepository prRepo;

    public ParkingService(ParkingRecordRepository prRepo) {
        this.prRepo = prRepo;
    }

    // Create new parking record if no active session
    // Return false when active parking session is found
    public boolean entry(String carPlate, LocalDateTime entryTime){
        Optional<ParkingRecord> parkingRecord = prRepo.findTopByCarPlateAndExitTimeIsNullOrderByEntryTimeDesc(carPlate);

        if (parkingRecord.isPresent()) {
            return false;
        } else{
            ParkingRecord newRecord = new ParkingRecord();
            newRecord.setCarPlate(carPlate);
            newRecord.setEntryTime(entryTime);
            prRepo.save(newRecord);

            return true;
        }
    }

    // Calculate and return parking fees based on latest active session
    // Return empty Optional if no active parking session is found
    public Optional<Float> exit(String carPlate, LocalDateTime exitTime){
        Optional<ParkingRecord> parkingRecord = prRepo.findTopByCarPlateAndExitTimeIsNullOrderByEntryTimeDesc(carPlate);

        if(parkingRecord.isEmpty()){
            return Optional.empty();
        } else {
            ParkingRecord activeRecord = parkingRecord.get();
            activeRecord.setExitTime(exitTime);
            // Save the updated record with exit time
            prRepo.save(activeRecord);

            float fee = calculateFee(activeRecord.getEntryTime(), activeRecord.getExitTime());
            return Optional.of(fee);
        }
    }

    // functions to calculate parking fee based on entry and exit time
    public float calculateFee(LocalDateTime entryTime, LocalDateTime exitTime){
        // For overnight case
        if (!entryTime.toLocalDate().equals(exitTime.toLocalDate())){
            return 50.0f;
        }

        Duration parkDuration = Duration.between(entryTime, exitTime);
        long parkHours = parkDuration.toHours();

        // Less than 1 hour
        if (parkHours < 1){
            return 0.0f;
        }

        // Calculate fee based on weekend/ weekday and parking hours
        DayOfWeek day = entryTime.getDayOfWeek();
        boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
        float fee = 0.0f;
        if(parkHours <= 3){
            fee = isWeekend ? 6.0f : 4.0f;
        } else {
            fee = (isWeekend ? 6.0f : 4.0f) + (parkHours - 3) * (isWeekend ? 2.0f : 1.0f);
        }

        return fee;
    }
}
