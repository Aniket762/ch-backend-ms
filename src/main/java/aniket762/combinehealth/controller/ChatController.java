package aniket762.combinehealth.controller;

import aniket762.combinehealth.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String ask(@RequestBody String question){
        return modelService.answer(question);
    }
}
