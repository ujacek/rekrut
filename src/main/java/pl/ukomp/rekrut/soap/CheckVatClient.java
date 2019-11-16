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


    /**
     * Sprawdzenie, czy podany numer VAT jest zarejestrowanym aktywnym podmiotem
     * gospodarczym w UE. Metoda korzysta z usługi SOAP
     * <a href="http://ec.europa.eu/taxation_customs/vies/checkVatService.wsdl">http://ec.europa.eu/taxation_customs/vies/checkVatService.wsdl</a>
     *
     * @param countryCode dwuznakowy kod kraku, np. PL, DE, IT, AU, ...
     * @param vatNumber prawidłowy dla danego kraju numer VAT
     * @return CheckVatResult - informacje o statusie VAT
     * (CheckVatResult.statusVat: true - jest zarejestrowany / false - nie ma)
     * lub null w przypadku niemożności ustalenia (n.p. błędu usługi SOAP).
     *
     * @author Jacek
     */
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
            log.error("ERROR (SOAP): {}: {}", ex.getClass(), ex.getMessage());
            throw ex; //FIXME: prostackie! powinna być własna klasa wyjatku
        }
        log.debug("Code = {}, VAT = {}, Status = {}, Name = {}, Address = {}", response.getCountryCode(),
                  response.getVatNumber(),
                  response.isValid(),
                  response.getName().getValue(),
                  response.getAddress().getValue());
        return new CheckVatResult(response.isValid());
    }

}
