package pl.ukomp.rekrut.dao.repo;

import java.util.Optional;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import pl.ukomp.rekrut.dao.model.Company;

/**
 * Testy integracyjne dla CompanyRepository
 *
 * @author Jacek
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class CompanyRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompanyRepository companyRepository;

    public CompanyRepositoryIT() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        Company company1 = Company.newInstance().withName("XXX").withCountryCode("YY").withVatNumber("1234567890");
        Company company2 = entityManager.persistAndFlush(company1);
        assertEquals(company1, company2);

        Optional<Company> found = companyRepository.findById(company2.getId());
        assertTrue(found.isPresent());

        assertEquals(company2, found.get());
    }

}
