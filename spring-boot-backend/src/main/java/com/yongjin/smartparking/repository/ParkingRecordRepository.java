package com.yongjin.smartparking.repository;

import com.yongjin.smartparking.model.ParkingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingRecordRepository extends JpaRepository<ParkingRecord, Long> {

    Optional<ParkingRecord> findTopByCarPlateAndExitTimeIsNullOrderByEntryTimeDesc(String carPlate);

}
