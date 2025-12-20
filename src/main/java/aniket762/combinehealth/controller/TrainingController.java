package aniket762.combinehealth.controller;

import aniket762.combinehealth.service.ModelService;
import aniket762.combinehealth.service.TrainingStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/model")
public class TrainingController {
    private final ModelService modelService;
    public TrainingController(ModelService modelService){
        this.modelService = modelService;
    }

    @PostMapping("/train")
    public Map<String,String> train(@RequestBody Map<String,String> body) throws Exception {
        String url = body.get("url");
        modelService.trainFromUrl(url);
        return Map.of("status","training_started");
    }

    @GetMapping("/status")
    public Map<String,String> status(){
        TrainingStatus status = modelService.getStatus();
        return Map.of("status",status.name(),"error", modelService.getLastError());
    }
}
