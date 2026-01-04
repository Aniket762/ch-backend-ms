package aniket762.combinehealth.startup;

import aniket762.combinehealth.service.ModelService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupTrainer implements CommandLineRunner {
    private final ModelService modelService;

    public StartupTrainer(ModelService modelService){
        this.modelService = modelService;
    }

    @Override
    public void run(String[] args) throws Exception{
        modelService.trainFromUrl("https://aeon.co/essays/the-hidden-role-of-pride-and-shame-in-the-human-hive");
    }
}
