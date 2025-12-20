package aniket762.combinehealth.controller;

import aniket762.combinehealth.service.ModelService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/agent")
public class ChatController {
    private final ModelService modelService;

    public ChatController(ModelService modelService){
        this.modelService = modelService;
    }

    @PostMapping("/chat")
    public String ask(@RequestBody String question){
        return modelService.answer(question);
    }
}
