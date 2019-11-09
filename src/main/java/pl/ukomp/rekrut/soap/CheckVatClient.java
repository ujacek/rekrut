package pl.ukomp.rekrut.soap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import pl.ukomp.rekrut.soap.checkvat.CheckVat;
import pl.ukomp.rekrut.soap.checkvat.CheckVatResponse;

@Slf4j
@Component
public class CheckVatClient {

    private static final String URL_EUROPAEUTAXATION_SERVICE = "http://ec.europa.eu/taxation_customs/vies/services/checkVatService";

    @Value("${eutax.service.url:" + URL_EUROPAEUTAXATION_SERVICE + "}")
    private String eutaxUrl;

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    public CheckVatResult verifyVatNumber(String countryCode, String vatNumber) {
        log.debug("verifyVatNumber({},{})", countryCode, vatNumber);
        CheckVat request = new CheckVat();
        request.setCountryCode(countryCode);
        request.setVatNumber(vatNumber);
        CheckVatResponse response = null;
        try {
            log.debug("url = {}", eutaxUrl);
            response = (CheckVatResponse) webServiceTemplate.marshalSendAndReceive(eutaxUrl, request);
        } catch (Exception ex) {
            //TODO: rozbudować obsługę błędów
            log.error("SOAP service error: {}: {}", ex.getClass(), ex.getMessage());
        }
        if (response == null) {
            return null;
        }
        log.debug("Country code : {}", response.getCountryCode());
        log.debug("VAT number : {}", response.getVatNumber());
        log.debug("Is valid : {}", response.isValid());
        log.debug("Name : {}", response.getName().getValue());
        log.debug("Address : {}", response.getAddress().getValue());
        return new CheckVatResult(response.isValid());
    }

}
