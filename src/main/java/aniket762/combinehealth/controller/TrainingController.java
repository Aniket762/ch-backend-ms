package aniket762.combinehealth.controller;
import aniket762.combinehealth.service.ModelService;
import aniket762.combinehealth.service.TrainingStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name="Train Model", description = "Train Model and Fetch Status of Training")
@RestController
@RequestMapping("/api/model")
public class TrainingController {
    private final ModelService modelService;
    public TrainingController(ModelService modelService){
        this.modelService = modelService;
    }

    @Operation(summary = "train model")
    @PostMapping("/train")
    public Map<String,String> train(@RequestBody Map<String,String> body) throws Exception {
        String url = body.get("url");
        modelService.trainFromUrl(url);
        return Map.of("status","training_started");
    }

 //   @Operation(summary = "get status")
   // @GetMapping("/status")
//    public Map<String,String> status(){
//        TrainingStatus status = modelService.getStatus();
//        Map<String,String> resp = new HashMap<>();
//        resp.put("status", status.name());
////        String err = modelService.getLastError();
////        resp.put("error", err == null ? "" : err);
//        return resp;
//    }
}
