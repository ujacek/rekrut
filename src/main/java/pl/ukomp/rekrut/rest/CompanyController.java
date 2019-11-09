package pl.ukomp.rekrut.rest;

import java.net.URI;
import pl.ukomp.rekrut.soap.CheckVatResult;
import pl.ukomp.rekrut.dao.model.Company;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.ukomp.rekrut.dao.repo.CompanyRepository;
import pl.ukomp.rekrut.soap.CheckVatClient;

@Slf4j
@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CheckVatClient checkVatClient;

//    @Cacheable("companies") //??? jaki ma sens buforowanie takiego zapytania? a z jakim kluczem?
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Company>> getAll() {
        log.debug("getAll()");
        Iterable<Company> iterable = companyRepository.findAll();
        List<Company> result = getListFromIterable(iterable);
        return ResponseEntity.ok(result);
    }

    @Cacheable("company")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Company> getById(@PathVariable("id") Integer id) {
        log.debug("getById({})", id);
        Optional<Company> result = companyRepository.findById(id);
        ResponseEntity response = ResponseEntity.of(result);
        return response;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Company> insert(@RequestBody @Valid Company company, BindingResult result) {
        log.debug("insert(): {}", company);
        if (result.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if ((company.getName() == null)
                || (company.getCountryCode() == null)
                || (company.getVatNumber() == null)) {
            //TODO: bardziej szczegółowa obsługa braku treści - zwrot statusu i komunikatu o przyczynie błędu
            return new ResponseEntity(HttpStatus.NO_CONTENT); //??? odpowiedni?
        }
        company.setId(null);
        try {
            company = companyRepository.save(company);
        } catch (Exception ex) {
            log.error("SAVE error: {}: {}", ex.getClass(), ex.getMessage());
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(company.getId()).toUri();
        return ResponseEntity.created(location).body(company);  //??? może zwracać tylko "location" bez company?
    }

    @CacheEvict(value = {"company", "checkVat"}, key = "#company.id")
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody @Valid Company company, BindingResult result) {
        log.debug("update(): {}", company);
        if (result.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Integer id = company.getId();
        if (!companyRepository.existsById(id)) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        companyRepository.save(company);
        return new ResponseEntity(HttpStatus.OK);
    }

    @CacheEvict(value = {"company", "checkVat"}, key = "#id")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("id") Integer id) {
        log.debug("delete({})", id);
        if (!companyRepository.existsById(id)) {
            return new ResponseEntity(HttpStatus.NOT_FOUND); //???
        }
        companyRepository.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Cacheable("checkVat")
    @RequestMapping(value = "/{id}/checkVat", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<CheckVatResult> checkVat(@PathVariable("id") Integer id) {
        log.debug("checkVat({})", id);
        Company company = companyRepository.findById(id).orElse(null);
        if (company == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND); //???
        }
        CheckVatResult result = checkVatClient.verifyVatNumber(company.getCountryCode(),
                                                               company.getVatNumber());
        if (result == null) {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE); //???: co ma zwrócić, gdy weryfikacja nie oddała wyniku?!
        }
        return ResponseEntity.of(Optional.of(result));
    }

    private static <T> List<T> getListFromIterable(Iterable<T> iterable) {
        log.debug("getListFromIterable()");
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

}