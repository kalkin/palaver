package de.xsrc.palaver.utils;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

public class BoundPropertySupport {
    private final PropertyChangeSupport changeHandler;
    private final Map<ObservableValue<?>, String> propertyNameMap;
    private final ChangeListener<Object> changeListener;

    public BoundPropertySupport(Object bean) {
        this.changeHandler = new PropertyChangeSupport(bean);
        this.propertyNameMap = new HashMap<>();
        this.changeListener = new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<? extends Object> observable,
                                Object oldValue, Object newValue) {
                String propertyName = BoundPropertySupport.this.propertyNameMap.get(observable);
                BoundPropertySupport.this
                        .changeHandler
                        .firePropertyChange(propertyName, oldValue, newValue);
            }
        };
    }

    public void raisePropertyChangeEventFor(Property property) {
        if (!this.propertyNameMap.containsKey(property)) {
            this.propertyNameMap.put(property, property.getName());
            property.addListener(this.changeListener);
        }
    }

    public void addChangeListener(PropertyChangeListener listener) {
        this.changeHandler.addPropertyChangeListener(listener);
    }

    public void removeChangeListener(PropertyChangeListener listener) {
        this.changeHandler.removePropertyChangeListener(listener);
    }
}