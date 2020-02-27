package io.rocketbase.commons.rsql;

import com.google.common.collect.Sets;
import io.rocketbase.commons.rsql.models.Person;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class PersonLogicalComboQueriesTest extends BaseIntegrationTest<Person> {

    @Test
    public void testEqualityOfTwoAndedThings() {
        check("firstName!=joe;lastName==dummy", "{ \"$and\" : [ { \"firstName\" : { \"$ne\" : \"joe\"}} , { \"lastName\" : \"dummy\"}]}");
    }

    @Test
    public void testThingsThatAreOredTogether() {
        check("firstName!=john,lastName==doe", "{ \"$or\" : [ { \"firstName\" : { \"$ne\" : \"john\"}} , { \"lastName\" : \"doe\"}]}");
    }

    @Test
    public void testAndingOfOringsOfAndings() {
        check("((firstName==john;lastName==doe),(firstName==aaron;lastName==carter));((age==21;height==90),(age==30;height==100))",
                "{ \"$and\" : [ { \"$or\" : [ { \"$and\" : [ { \"firstName\" : \"john\"} , " +
                        "{ \"lastName\" : \"doe\"}]} , { \"$and\" : [ { \"firstName\" : \"aaron\"} , " +
                        "{ \"lastName\" : \"carter\"}]}]} , { \"$or\" : [ { \"$and\" : [ { \"age\" : 21} , " +
                        "{ \"height\" : 90}]} , { \"$and\" : [ { \"age\" : 30} , { \"height\" : 100}]}]}]}");
    }

    @Test
    public void testSelector() {
        Set<String> selectors = adapter.getSelectors("((firstName==john;lastName==doe),(firstName==aaron;lastName==carter));((age==21;height==90),(age==30;height==100))");
        assertEquals(selectors, Sets.newHashSet("firstName", "lastName", "age", "height"));
    }

    @Test
    public void testIsValid() {
        assertEquals(adapter.isValid("((firstNamejohn;lastName==doe"), false);
        assertEquals(adapter.isValid("(firstName==aaron;lastName==carter)"), true);
        assertEquals(adapter.isValid("(firstName==aaron;lastName==carter)", "firstName", "lastName"), true);
        assertEquals(adapter.isValid("(firstName==aaron;lastName==carter)", "firstName"), false);
        assertEquals(adapter.isValid("(firstName==aaron)", "firstName", "lastName"), true);
    }

}
