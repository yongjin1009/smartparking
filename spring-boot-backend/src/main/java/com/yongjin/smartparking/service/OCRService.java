package com.yongjin.smartparking.service;

import com.yongjin.smartparking.config.AppConfig;
import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


@Service
public class OCRService {
    private AppConfig config;

    public OCRService(AppConfig config) {
        this.config = config;
    }

    // Call external API to extract and return CarPlate number
    // Return null if CarPlate number is not found
    public String extractCarPlate(MultipartFile file) {
        try{
            String url = config.getUrl() + "/extract_plate/";

            RestTemplate restTemplate = new RestTemplate();

            ByteArrayResource resource = new ByteArrayResource(file.getBytes()){
                @Override
                public String getFilename() {
                    return file.getOriginalFilename(); // Important: override to provide filename
                }
            };

            // Create body with file
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);  // The key "file" should match the server-side param name

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if(response.getStatusCode() == HttpStatus.OK){
                JSONObject json = new JSONObject(response.getBody());
                return json.getString("CarPlate");
            } else {
                return null;
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
