package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootStrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// 我们这里测试的是BeerRepository, 直接call BeerRepository的方法, 所以就是在entities这个package下的Beer.java的validation
@DataJpaTest
@Import({BootStrapData.class, BeerCsvServiceImpl.class}) // we want BootStrapData and BeerCsvServiceImpl to be injected into the BeerRepository , otherwise this test will only search in H2 in memory database,  so the autowire for BeerRepository will not work
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    public void testSaveBeer() {
        Beer savedBeer = beerRepository.save(Beer.builder()
                .beerName("My beer")
                        .beerStyle(BeerStyle.PALE_ALE)
                        .upc("123456789")
                        .price(new BigDecimal("12.99"))
                .build());
        // this is to tell Hibernate to immediately write to the DB, otherwise our Beer JPA validation will not work(since the test ends too quickly and will not spot the change)
        beerRepository.flush();
        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }

    @Test
    public void testSaveBeerTooLong() {

        assertThrows(ConstraintViolationException.class , () -> {
            Beer savedBeer = beerRepository.save(Beer.builder()
                    .beerName("My beer 32t647t2332t647t23646234732t647t23646234732t647t23646234732t647t236432t647t23646234732t647t23646234732t647t23646234732t647t2364623476234732t647t23646234732t647t236462347646234762")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("123456789")
                    .price(new BigDecimal("12.99"))
                    .build());
            // this is to tell Hibernate to immediately write to the DB, otherwise our Beer JPA validation will not work(since the test ends too quickly and will not spot the change)
            beerRepository.flush();
        });
    }

    @Test
    void testGetBeerListByName() {

        Page<Beer> list = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%", null); // %IPA% means return any beer that has the word IPA in its name
        assertThat(list.getContent().size()).isEqualTo(336);
    }
}