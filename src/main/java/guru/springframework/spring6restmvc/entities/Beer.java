package guru.springframework.spring6restmvc.entities;

import guru.springframework.spring6restmvc.model.BeerStyle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Beer{

    @Id // 为了使用UUID在Hibernate, need the following annotation
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name="UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.CHAR) // to use MYSQL, we need this annotation
    @Column(length = 36,columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @Version // 需要这个annotation for Hibernate to check the version of DB to avoid outdated data!
    private Integer version;

    // it's much better to handle the validation though a bean validation constraint, rather than later
    // invalid data being persisted to the DB and cause problems
    @NotNull
    @NotBlank
    @Size(max=50) // ConstraintViolationException will be thrown if the size is greater than 50
    @Column(length = 30) // 改变默认的255 to 50
    private String beerName;
    @NotNull
    private BeerStyle beerStyle;
    @NotNull
    @NotBlank
    @Size(max = 255) // best practice to match the size of the column in the DB
    private String upc;
    private Integer quantityOnHand;
    @NotNull
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "beer")
    private Set<BeerOrderLine> beerOrderLines;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "beer_category",
            joinColumns = @JoinColumn(name = "beer_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();  // many-to-many要initialization: 防止在别地方的tranaction中被call 没被initialized问题

    // 这种操作使得我们只有一个这个method就能把双方关系建立起来
    public void addCategory(Category category) {
        this.categories.add(category);
        category.getBeers().add(this);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getBeers().remove(category);
    }
}
