package guru.springframework.spring6restmvc.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.controller.BeerController;
import guru.springframework.spring6restmvc.controller.NotFoundException;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// intelliJ可能需要手动输入the following
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



// MVC test primarily focuses on the controller layer of the application. It is used to test the controller layer of the application without starting the server.
// 我们会使用 given method 来预设behavior, 然后用verify来确认预期的behavior
@WebMvcTest(BeerController.class)
public class BeerControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    MockMvc mockMvc; // use MockMvc context

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ObjectMapper objectMapper; //  here we can serialize and deserialize: POJO-> Jason,  or   Jason -> POJO

    @MockBean  //(add the dependency) tell the Mockito to bring a mock of dependency into the BeerController
    BeerService beerService;

   BeerServiceImpl beerServiceImpl;

    @BeforeEach // setUp method 用于在这个test class每次在 call 不同的test method 系统会自动call这里, 帮助还原 initialization
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }


    @Test
    void getBeerById() throws Exception {
        BeerDTO testBeer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);

        //This line uses Mockito's given method to specify the behavior of the beerService mock when the getBeerById method is called
        // and will return beerServiceImpl.listBeers().get(0);
        given(beerService.getBeerById(testBeer.getId())).willReturn(Optional.of(testBeer));

        // test get method
        mockMvc.perform(get("/api/v1/beer/" + testBeer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())   // 确保status正确
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 确保返回JASON type
                .andExpect(jsonPath("$.id", is(testBeer.getId().toString())))  //手动import: import static org.hamcrest.core.Is.is;
                .andExpect(jsonPath("$.beerName", is(testBeer.getBeerName())));  // perform assertion against Jason document
    }

    @Test
    void testListBeers() throws Exception {
        given(beerService.listBeers(any(), any() , any(), any(), any()))
                .willReturn(beerServiceImpl.listBeers(null, null, false, 1, 25));

        mockMvc.perform(get(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()", is(3)));
    }



    @Test
    void testUpdateBeer() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);

        //This line uses Mockito's given method to specify the behavior of the beerService mock when the updateBeerById method is called with any UUID and any Beer object.
        // 因为我们在beerServiceImpl 的beer id是随机生成的, 而在BootStrapData里面, 我们只有3个beer没有对应的id, 也就是根据这里传入的id, 在BeerServiceJPa which is a primary bean里面的beerRepository肯定
        // 找不到对应的id, 所以会返回一个empty的Optional
        given(beerService.updateBeerById(any(), any())).willReturn(Optional.of(beer));

        mockMvc.perform(put("/api/v1/beer/"+ beer.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beer)))  // The .content() method in the mockMvc.perform() call is used to set the request body content. In this case, it is being used to set the content of the HTTP PUT request to a JSON representation of the beer object.
                .andExpect(status().isNoContent());

        // verify that beerService.updateBeerById was called one time and parameters are going to be any ID and any Beer
        verify(beerService).updateBeerById(any(UUID.class), any(BeerDTO.class));
    }

    @Test
    void testCreateNewBeer() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper(); //  here we can serialize and deserialize: POJO-> Jason,  or   Jason -> POJO
//        objectMapper.findAndRegisterModules(); // need this configuration for date-time type
//  上面的方法不太推荐, instead, we use 这个parent class 直接autowired的 ObjectMapper.

        BeerDTO beer = new BeerServiceImpl().listBeers(null, null, false, 1, 25).getContent().get(0);
        beer.setVersion(null);
        beer.setId(null);

        // 能够提前预设 behavior!
        //This line uses Mockito's given method to specify the behavior of the beerService mock when the saveNewBeer method is called with any Beer object (any(Beer.class)).
        //It states that when saveNewBeer is called(只是方便test), it will return the second beer in the list returned by beerServiceImpl.listBeers() (index 1)
        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(1));

        mockMvc.perform(post("/api/v1/beer")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;  // make it reusable

    @Captor
    ArgumentCaptor<BeerDTO> beerArgumentCaptor;

    @Test
    void testDeleteBeer() throws Exception {
        BeerDTO beer= beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);

        // the reason to use return true is that we are not testing the actual delete method, we are testing the controller
        // since the signature of deleteByid, and Mockito is going to return the default value of the return type, which is false
        // here we have to specify the return value to be true so that the test will pass, 不然controller 会抛出一个exception, 后面就accept不了了
        given(beerService.deleteById(any(UUID.class))).willReturn(true);

        mockMvc.perform(delete("/api/v1/beer/"+beer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(beerService).deleteById(uuidArgumentCaptor.capture()); //        // verify that beerService.deleteById was called one time and parameters are going to be captured

        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }

    @Test
    void testPatchBeer() throws Exception {
        BeerDTO beer  = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);

        Map<String, Object> beerMap = new HashMap<>(); // 利用Map来模拟 Jason形式, 方便我们默认的model getBeerName() 有效!
        beerMap.put("beerName", "Casonsss");

        mockMvc.perform(patch("/api/v1/beer/" + beer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap)))
                .andExpect(status().isNoContent());

        verify(beerService).patchBeerById(uuidArgumentCaptor.capture(),beerArgumentCaptor.capture());

        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(beerMap.get("beerName")).isEqualTo(beerArgumentCaptor.getValue().getBeerName());
    }

    @Test
    void getBeerByIdNotFound() throws Exception {

            // 设计的故意会throw一个exception
        given(beerService.getBeerById(any(UUID.class))).willThrow(NotFoundException.class);

        mockMvc.perform(get(BeerController.BEER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }


    // here test the validation, (几个要求not null 的attribute, 会返回BadRequest如果data is invalid)
    @Test
    void testCreateBeerNullAttributes() throws Exception {
        BeerDTO beerDTO = BeerDTO.builder().build();

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(1));

        MvcResult mvcResult =  mockMvc.perform(post(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTO))) // content() method in the mockMvc.perform() call is used to set the request body content. In this case, it is being used to set the content of the HTTP POST request to a JSON representation of the beer object.
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(6)))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void testUpdateBeerNullAttributes() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);
        beer.setBeerName(" ");

        given(beerService.updateBeerById(any(), any())).willReturn(Optional.of(beer));

        mockMvc.perform(put("/api/v1/beer/"+ beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }

}
