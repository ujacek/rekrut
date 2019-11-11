package pl.ukomp.rekrut.soap;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Wynik sprawdzenia, czy numer identyfikacyjny VAT jest zarejestrowany w Unii Europejskiej.
 * 
 * @author Jacek
 */
@AllArgsConstructor
@Getter
@ApiModel(description = "Wynik sprawdzenia, czy numer identyfikacyjny VAT jest zarejestrowany w Unii Europejskiej.")
public class CheckVatResult {

    @ApiModelProperty(notes = "Wynik sprawdzenia: true - zarejestrowany w UE / false - niezarejestrowany")
    private final boolean statusVat;
}
