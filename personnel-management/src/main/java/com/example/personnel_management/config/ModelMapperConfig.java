package com.example.personnel_management.config;

import com.example.personnel_management.converter.CollectionToListConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {


    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new CollectionToListConverter());
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    /** Transformer une entité JPA en DTO (pour l’envoyer au frontend).
     Transformer un DTO en entité (avant de l’enregistrer dans la base de données).
     Éviter les erreurs liées à Hibernate en convertissant des structures spécifiques (PersistentBag → List).*/
}
