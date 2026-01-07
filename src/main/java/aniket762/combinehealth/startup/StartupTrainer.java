package aniket762.combinehealth.startup;

import aniket762.combinehealth.service.ModelService;
import aniket762.combinehealth.util.Utils;
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
        System.out.println("Starting training...");

        String url = "https://www.uhcprovider.com/en/policies-protocols/commercial-policies/commercial-medical-drug-policies.html";
        String article = Utils.readFromUrl(url);
        article = Utils.normalize(article);

        modelService.trainFromUrl(article);

        System.out.println("Training finished. Model READY!");
    }
}