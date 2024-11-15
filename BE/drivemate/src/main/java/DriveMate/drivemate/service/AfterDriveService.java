package DriveMate.drivemate.service;

import DriveMate.drivemate.DTO.AddressRespondDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AfterDriveService {
    private final RestTemplate restTemplate;

    @Value("${tmap.api.key}") // application.properties 파일에 API 키를 저장
    private String appKey;

    @Autowired
    public AfterDriveService(RestTemplateBuilder restTemplate){
        this.restTemplate = new RestTemplate();
    }


}
