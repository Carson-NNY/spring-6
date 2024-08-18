package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Carson
 * @Version
 */
@Slf4j
@RequiredArgsConstructor
@RestController // 变成Rest MVC controller: it will return back JSON instead of html!
public class BeerController {

    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final BeerService beerService;

//    @RequestMapping("/api/v1/beer")  这个可以挪到这个class上面, 这里就可以只声明下面的, 省去很多repetitive code
    // note: Page 类型是选择的 springframework.data.domain那个
    @GetMapping(value = BEER_PATH)
    public Page<BeerDTO> listBeers(@RequestParam(required = false) String beerName,
                                   @RequestParam(required = false) BeerStyle beerStyle,
                                   @RequestParam(required = false) Boolean showInventory,
                                   @RequestParam(required = false) Integer pageNumber,
                                   @RequestParam(required = false) Integer pageSize){ // 这样@RequestParam(required = false)可以不传入, 我们就list所有beer, 也可以传入beerName, list beer with name
        // 因为上面的@RestController, 这里从service返回的list will be converted to JSON
        return beerService.listBeers(beerName,beerStyle,showInventory, pageNumber, pageSize);
    }

    // 如果path有需要extract的值, 先在这里 map出来
//    @RequestMapping(value = "{beerId}", method = RequestMethod.GET) //  method = RequestMethod.GET 这个应该是默认的不写也行
    @GetMapping(value = BEER_PATH_ID)
    public BeerDTO getBeerById(@PathVariable("beerId") UUID id){

        log.debug("BeerService getBeerById is called");
        // 如果找不到, 就抛出一个自定义的exception, we need to throw it at controller level
        return beerService.getBeerById(id).orElseThrow(NotFoundException::new);
    }

//    @RequestMapping(method = RequestMethod.POST)
    @PostMapping(BEER_PATH) // alternate way for post handling
    public ResponseEntity handlePost(@Validated @RequestBody BeerDTO beer){// annotated with @Validated, which ensures that the BeerDTO object passed in the request body is validated according to the constraints defined on its fields
        BeerDTO savedBeer =  beerService.saveNewBeer(beer);

        // best practice to return the header with the location of the object just created
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BEER_PATH + "/" + savedBeer.getId().toString());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PutMapping(BEER_PATH_ID)
    public ResponseEntity updateById(@PathVariable("beerId")UUID beerId, @Validated  @RequestBody BeerDTO beer){
        if(beerService.updateBeerById(beerId,beer).isEmpty()){
            throw new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BEER_PATH_ID)
    public ResponseEntity deleteById(@PathVariable("beerId") UUID beerId){
        if(!beerService.deleteById(beerId)) {
            throw new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    // rarely used patch method
    @PatchMapping(BEER_PATH_ID)
    public ResponseEntity updateBeerPatchById(@PathVariable("beerId") UUID beerId,  @RequestBody BeerDTO beer){
        // why @Validated is omitted here: Custom Validation Logic by using handleJPAViolations in CustomErrorController
        beerService.patchBeerById(beerId,beer);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


}
