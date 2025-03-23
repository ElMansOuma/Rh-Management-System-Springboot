package com.example.personnel_management.converter;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionToListConverter implements Converter<Collection<?>, List<?>> {

    @Override
    public List<?> convert(MappingContext<Collection<?>, List<?>> context) {
        Collection<?> source = context.getSource();
        if (source == null) {
            return null;
        }
        return new ArrayList<>(source);
    }
}