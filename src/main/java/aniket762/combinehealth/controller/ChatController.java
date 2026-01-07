package aniket762.combinehealth.controller;

import aniket762.combinehealth.service.ModelService;
import aniket762.combinehealth.service.TrainingStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name="Chat End Point", description = "User gives query gets agent's response")
@RestController
@RequestMapping("api/agent")
public class ChatController {

    private final ModelService modelService;

    public ChatController(ModelService modelService){
        this.modelService = modelService;
    }

    @Operation(summary = "Conversational Endpoints")
    @PostMapping("/chat")
    public String ask(
            @RequestParam String sessionId,
            @RequestBody String question
    ) {
        if (modelService.getStatus() == TrainingStatus.TRAINING) {
            return "⏳ Please wait, model is being trained...";
        }

        if (modelService.getStatus() == TrainingStatus.NOT_TRAINED) {
            return "⚠️ Model not trained yet.";
        }

        if (modelService.getStatus() == TrainingStatus.FAILED) {
            return "❌ Model training failed. Please retrain.";
        }

        return modelService.answer(sessionId,question);
    }
}
