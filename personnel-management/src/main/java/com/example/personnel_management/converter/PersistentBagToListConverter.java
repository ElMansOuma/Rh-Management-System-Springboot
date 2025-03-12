package com.example.personnel_management.converter;

import org.hibernate.collection.spi.PersistentBag;
import org.modelmapper.AbstractConverter;

import java.util.ArrayList;
import java.util.List;

public class PersistentBagToListConverter extends AbstractConverter<PersistentBag, List> {
    @Override
    protected List convert(PersistentBag source) {
        if (source == null) {
            return null;
        }
        return new ArrayList<>(source);
    }
}