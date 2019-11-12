package pl.ukomp.rekrut.dao.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import pl.ukomp.rekrut.dao.model.Company;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Definicja repozytorium operującego na tabeli Company z id typu Integer.
 *
 * @author Jacek
 */
//public interface CompanyRepository extends CrudRepository<Company, Integer> {
public interface CompanyRepository extends PagingAndSortingRepository<Company, Integer> {

    /**
     * Zwraca rekord Company znaleziony dla podanego kodu kraju i numeru VAT.
     *
     * @param countryCode kod kraju
     * @param vatNumber numer VAT
     * @return znaleziony rekord Company lub Optional#empty() gdy takiego brak
     */
    @Query("SELECT c FROM Company c where c.countryCode = :countryCode and c.vatNumber = :vatNumber")
    Optional<Company> findByCountryCodeAndVatNumber(@Param("countryCode") String countryCode, @Param("vatNumber") String vatNumber);

}
