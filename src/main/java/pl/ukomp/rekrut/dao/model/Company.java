package pl.ukomp.rekrut.dao.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

/**
 * Klasa definiująca podstawowe atrybuty podmiotu gospodarczego, zapisywane w
 * bazie danych.
 *
 * @author Jacek
 */
@Entity
@Table(name = "Company",
       uniqueConstraints = @UniqueConstraint(columnNames = {"countryCode", "vatNumber"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@With
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ApiModel(description = "Podstawowe dane podmiotu gospodarczego")
public class Company implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "Klucz ID generowany przez bazę danych")
    private Integer id;

    @Column(name = "name", nullable = false)
    @ApiModelProperty(notes = "Nazwa podmiotu gospodarczego")
    private String name;

    @Column(name = "countryCode", length = 2, nullable = false)
    @EqualsAndHashCode.Include
    @ApiModelProperty(notes = "Dwuznakowy kod kraju")
    private String countryCode;

    @Column(name = "vatNumber", length = 12, nullable = false)
    @EqualsAndHashCode.Include
    @ApiModelProperty(notes = "Numer identyfikacyjny VAT")
    private String vatNumber;

    private static final long serialVersionUID = 1L;

    public static Company newInstance() {
        return new Company();
    }

}
