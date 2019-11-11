package pl.ukomp.rekrut.soap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Klasa konfigurująca wykorzystywana w aplikacji usługę SOAP.
 *
 * @author Jacek
 */
@Configuration
public class SoapConfig {

//    private static final String URL_EUROPAEUTAXATION_SERVICE = "http://ec.europa.eu/taxation_customs/vies/services/checkVatService";
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this is the package name specified in the <generatePackage> specified in pom.xml
        marshaller.setContextPath("pl.ukomp.rekrut.soap.checkvat");
        return marshaller;
    }


    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller());
        webServiceTemplate.setUnmarshaller(marshaller());
//        webServiceTemplate.setDefaultUri(URL_EUROPAEUTAXATION_SERVICE);
        return webServiceTemplate;
    }
}
