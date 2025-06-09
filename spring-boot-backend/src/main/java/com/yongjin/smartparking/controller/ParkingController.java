package com.yongjin.smartparking.controller;


import com.yongjin.smartparking.service.OCRService;
import com.yongjin.smartparking.service.ParkingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/api/")
public class ParkingController {
    private final ParkingService parkingService;
    private final OCRService ocrService;

    public ParkingController(ParkingService parkingService, OCRService ocrService) {
        this.parkingService = parkingService;
        this.ocrService = ocrService;
    }

    @PostMapping("/entry")
    public ResponseEntity<String> carEntry(@RequestParam("image") MultipartFile image, @RequestParam(value = "time", required = false) String time ){
        String carPlate = ocrService.extractCarPlate(image);

        if(carPlate != null){
            LocalDateTime ldt;

            if(time == null || time.trim().isEmpty()){
                ldt = LocalDateTime.now();
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                ldt = LocalDateTime.parse(time, formatter);
            }

            boolean success = parkingService.entry(carPlate, ldt);

            if (success){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String ldtString = ldt.format(formatter);

                return new ResponseEntity<>(carPlate + " entered at " + ldtString , HttpStatus.OK);
            } else {
                return new ResponseEntity<>(carPlate + " has active parking session", HttpStatus.BAD_REQUEST);
            }

        } else {
            return new ResponseEntity<>("Car Plate not detected", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/exit")
    public ResponseEntity<String> carExit(@RequestParam("image") MultipartFile image, @RequestParam(value="time", required=false) String time) {
        String carPlate = ocrService.extractCarPlate(image);

        if(carPlate != null){
            LocalDateTime ldt;

            if(time == null || time.trim().isEmpty()){
                ldt = LocalDateTime.now();
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                ldt = LocalDateTime.parse(time, formatter);
            }

            Optional<Float> fee = parkingService.exit(carPlate, ldt);

            if(fee.isPresent()){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String ldtString = ldt.format(formatter);

                return new ResponseEntity<>(carPlate + " exit at " + ldtString + " fee = RM" + String.format("%.2f", fee.get()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(carPlate + " no active parking session", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Car Plate not detected", HttpStatus.BAD_REQUEST);
        }
    }
}
