package pl.ukomp.rekrut;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;
import pl.ukomp.rekrut.dao.model.Company;

/**
 *
 * @author Jacek
 */
public class CompanyTest {

    public CompanyTest() {
    }

    /**
     * Test of setId and getId methods, of class Company.
     */
    @Test
    public void testId() {
        Company instance = new Company();
        Integer id = 1;
        instance.setId(id);
        Integer result = instance.getId();
        assertEquals(id, result);
    }

    /**
     * Test of setName and getName methods, of class Company.
     */
    @Test
    public void testName() {
        Company instance = new Company();
        String name = "_name";
        instance.setName(name);
        String result = instance.getName();
        assertEquals(name, result);
    }

    /**
     * Test of setCountryCode and getCountryCode methods, of class Company.
     */
    @Test
    public void testCountryCode() {
        Company instance = new Company();
        String countryCode = "_countryCode";
        instance.setCountryCode(countryCode);
        String result = instance.getCountryCode();
        assertEquals(countryCode, result);
    }

    /**
     * Test of setVatNumber and getVatNumber methods, of class Company.
     */
    @Test
    public void testVatNumber() {
        Company instance = new Company();
        String vatNumber = "_vatNumber";
        instance.setVatNumber(vatNumber);
        String result = instance.getVatNumber();
        assertEquals(vatNumber, result);
    }

    /**
     * Test of equals method, of class Company.
     */
    @Test
    public void testEquals() {
        Company item0 = new Company();

        Company item1 = new Company(1, "_Name1", "_CountryCode", "_VatNumber");
        Company item2 = new Company(2, "_Name2", "_CountryCode", "_VatNumber");
        Company item3 = new Company(3, "_Name3", "_CountryCode", "_VatNumber");

        Company item4 = new Company(1, "_Name1", "_CountryCodeX", "_VatNumber");
        Company item5 = new Company(1, "_Name1", "_CountryCode", "_VatNumberX");

        assertTrue(item1.equals(item2));
        assertTrue(item1.equals(item3));
        assertTrue(item2.equals(item1));
        assertTrue(item2.equals(item3));
        assertTrue(item3.equals(item1));
        assertTrue(item3.equals(item2));

        assertFalse(item1.equals(null));
        assertFalse(item1.equals(item0));
        assertFalse(item1.equals(item4));
        assertFalse(item1.equals(item5));
    }

    /**
     * Test of hashCode method, of class Company.
     */
    @Test
    public void testHashCode() {
        Company item1 = new Company(1, "_Name1", "_CountryCode", "_VatNumber");
        Company item2 = new Company(1, "_Name1", "_CountryCode", "_VatNumber");
        Company item3 = new Company(1, "_Name1", "_CountryCode", "_VatNumber");

        Company item4 = new Company(1, "_Name1", "_CountryCodeX", "_VatNumber");
        Company item5 = new Company(1, "_Name1", "_CountryCode", "_VatNumberX");

        assertEquals(item1.hashCode(), item2.hashCode());
        assertEquals(item1.hashCode(), item3.hashCode());
        assertEquals(item2.hashCode(), item3.hashCode());

        assertNotEquals(item1.hashCode(), item4.hashCode());
        assertNotEquals(item1.hashCode(), item5.hashCode());
    }

}
