package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerDTO;
import org.mapstruct.Mapper;


// 我们创建完这个Mapper interface后点开 Maven -> Lifecycle -> clean ,
// then compile -> then we have target package -> generated-sources里可以找到 mapstruct帮助我们
// generated implementation for later injection use!

@Mapper
public interface BeerMapper {
// the reason we need this interface is that we need to convert between Beer and BeerDTO
    Beer beerDtoToBeer(BeerDTO dto);

    BeerDTO beerToBeerDto(Beer beer);
}
