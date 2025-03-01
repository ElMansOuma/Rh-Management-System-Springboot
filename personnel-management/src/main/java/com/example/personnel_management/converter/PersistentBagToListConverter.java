package com.example.personnel_management.converter;

import org.hibernate.collection.spi.PersistentBag;
import org.modelmapper.AbstractConverter;
import java.util.List;
import java.util.ArrayList;

public class PersistentBagToListConverter extends AbstractConverter<PersistentBag, List<?>> {

    @Override
    protected List<?> convert(PersistentBag source) {
        return new ArrayList<>(source);  // Convertir le PersistentBag en une ArrayList simple
    }
}
