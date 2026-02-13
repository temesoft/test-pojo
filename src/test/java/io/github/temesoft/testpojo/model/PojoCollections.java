package io.github.temesoft.testpojo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PojoCollections {

    private Hashtable<String, Collection<?>> items0;
    private Hashtable<String, Collection<Number>> items1;
    private Map<String, List<?>> items2;
    private Map<String, List<SomeInterface>> items3;
    private Map<String, SomeInterface> items4;
    private Map<SomeInterface, String> items5;
    private Map<?, ?> items6;
    private Map<String, ?> items7;
    private Collection<?> items8;
    private Collection<SomeInterface> items9;
    private Set<SomeInterface> items10;
    private TreeSet<SomeInterface> items11;
    private Set<?> items12;
    private TreeSet<? extends Number> items13;
    private List<SomeInterface> items14;
    private ArrayList<SomeInterface> items15;
    private List<?> items16;
    private ArrayList<?> items17;
    private Collection<? extends Number> items18;
    private Collection<? extends SomeInterface> items19;
    private List<PojoExtendingAbstractBase> items20;
    private List<? extends PojoExtendingAbstractBase> items21;
    private Map<PojoExtendingAbstractBase, ?> items22;
    private Map<SomeInterface, ? extends PojoExtendingAbstractBase> items23;

}
