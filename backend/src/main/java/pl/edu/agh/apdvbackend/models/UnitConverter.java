package pl.edu.agh.apdvbackend.models;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "unit_converter")
@Getter
@Setter
@NoArgsConstructor
public class UnitConverter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(required = true)
    private Long id;

    @Schema(required = true)
    @ManyToOne
    @JoinColumn(name = "from_unit_id")
    private Unit fromUnit;

    @Schema(required = true)
    @ManyToOne
    @JoinColumn(name = "to_unit_id")
    private Unit toUnit;

    @Schema(required = true)
    private int calculationStep;

    @Schema(required = true)
    private MathOperation mathOperation;

    @Schema(required = true)
    private Double value;
}
