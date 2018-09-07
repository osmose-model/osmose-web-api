package fr.ird.osmose.web.api;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ValueFactoryProxyTest {

    @Test
    public void addIfDefault() {
        ValueFactory valueFactoryNull = (name, groupName) -> null;
        ValueFactory valueFactoryName = (name, groupName) -> name;
        ValueFactoryProxy proxy = new ValueFactoryProxy(Arrays.asList(valueFactoryNull, valueFactoryName));
        assertThat(proxy.groupValueFor("foo", new Group()), is("foo"));
    }

    @Test
    public void notAddIfSet() {
        ValueFactory valueFactoryBar = (name, groupName) -> "bar";
        ValueFactory valueFactoryName = (name, groupName) -> name;
        ValueFactoryProxy proxy = new ValueFactoryProxy(Arrays.asList(valueFactoryBar, valueFactoryName));
        assertThat(proxy.groupValueFor("foo", new Group()), is("bar"));
    }
}