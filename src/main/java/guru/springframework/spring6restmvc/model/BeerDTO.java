package guru.springframework.spring6restmvc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by jt, Spring Framework Guru.
 */
@Builder  // 这里加了Builder method后, 在services里面的implementation可以使用builder() pattern for cleaner code
@Data // Lombok annotation: Equivalent to @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode.
public class BeerDTO {
    // DTO stands for Data Transfer Object, it is used to transfer data between software application subsystems.
    // DTOs are often used in conjunction with data access objects to retrieve data from a database.
    // the reason we need this is because we don't want to expose the entity to the outside world, we want to expose the DTO.
    // in Controller, we deal with DTO, in H2 DB, we deal with Entity.
    private UUID id; // 我们通常不会把id暴露给外部, 所以这里的id通常会被设置为null
    private Integer version;

    @NotNull
    @NotBlank // this is to avoid blank space (空格), need to make sure that on controller, we add @Validated to
    // the request methods to make sure here the validation is done
    private String beerName;
    @NotNull
    private BeerStyle beerStyle;
    @NotNull
    @NotBlank // notblank 仅针对string, notnull是针对object
    private String upc;
    private Integer quantityOnHand;
    @NotNull
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
}
