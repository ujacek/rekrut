package pl.ukomp.rekrut.rest;

import java.net.URI;
import pl.ukomp.rekrut.soap.CheckVatResult;
import pl.ukomp.rekrut.dao.model.Company;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    private static final String CACHE_NAME_ALL = "companies";
    private static final String CACHE_NAME_COMPANY = "company";
    private static final String CACHE_NAME_CHECKVAT = "checkVat";


    @Cacheable(cacheNames = CACHE_NAME_ALL)
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Company>> getAll(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                @RequestParam(name = "size", required = false) Integer size) {
        log.debug("getAll({},{})", page, size);
        Pageable pageRequest = getPageable(page, size);
        log.debug("efektywny pageRequest = {}", pageRequest);
        Page<Company> pageCompany = companyRepository.findAll(pageRequest);
        List<Company> result = pageCompany.getContent();
        return ResponseEntity.ok(result);
    }


    private static Pageable getPageable(Integer page, Integer size) {
        if (size == null) {
            return Pageable.unpaged();
        } else {
            return PageRequest.of(page, size);
        }
    }


    @Cacheable(cacheNames = CACHE_NAME_COMPANY)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Company> getById(@PathVariable("id") Integer id) {
        log.debug("getById({})", id);
        Optional<Company> result = companyRepository.findById(id);
        ResponseEntity response = ResponseEntity.of(result);
        return response;
    }


    @CacheEvict(cacheNames = CACHE_NAME_ALL, allEntries = true)
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Company> insert(@RequestBody @Valid Company company, BindingResult result) {
        log.debug("insert(): {}", company);
        if (result.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if ((company == null)
                || (company.getName() == null)
                || (company.getCountryCode() == null)
                || (company.getVatNumber() == null)) {
            //TODO: bardziej szczegółowa obsługa braku treści - zwrot statusu i komunikatu o przyczynie błędu
            return new ResponseEntity(HttpStatus.BAD_REQUEST); //FIXME: ?? odpowiedni?
        }
        Optional<Company> found = companyRepository.findByCountryCodeAndVatNumber(company.getCountryCode(), company.getVatNumber());
        if (found.isPresent()) {
            log.debug("Istnieje juz {}!", found.get());
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        company.setId(null);
        company = companyRepository.save(company);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(company.getId()).toUri();
        return ResponseEntity.created(location).body(company);  //FIXME: ?? a może zwracać tylko "location" bez company?
    }


    @Caching(evict = {
        @CacheEvict(cacheNames = {CACHE_NAME_COMPANY, CACHE_NAME_CHECKVAT}, key = "#company.id"),
        @CacheEvict(cacheNames = CACHE_NAME_ALL, allEntries = true)
    })
    @RequestMapping(value = "", method = RequestMethod.PUT)
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


    @Caching(evict = {
        @CacheEvict(cacheNames = {CACHE_NAME_COMPANY, CACHE_NAME_CHECKVAT}, key = "#id"),
        @CacheEvict(cacheNames = CACHE_NAME_ALL, allEntries = true)
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("id") Integer id) {
        log.debug("delete({})", id);
        if (!companyRepository.existsById(id)) {
            return new ResponseEntity(HttpStatus.NOT_FOUND); //FIXME: ??
        }
        companyRepository.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);   //zwracane dla wykonanego DELETE!
    }


    @Cacheable(cacheNames = CACHE_NAME_CHECKVAT)
    @RequestMapping(value = "/{id}/checkVat", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<CheckVatResult> checkVat(@PathVariable("id") Integer id) {
        log.debug("checkVat({})", id);
        Company company = companyRepository.findById(id).orElse(null);
        if (company == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND); //FIXME: ??
        }
        CheckVatResult result = checkVatClient.verifyVatNumber(company.getCountryCode(),
                                                               company.getVatNumber());
        if (result == null) {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE); //FIXME: ??: co ma zwrócić, gdy weryfikacja nie oddała wyniku?!
        }
        return ResponseEntity.of(Optional.of(result));
    }

}
