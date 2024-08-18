package guru.springframework.spring6restmvc.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Carson
 * @Version
 */

// if anything those controller are throwed a notfoundException, it will be handle by the handler method here,
@ControllerAdvice  // set it as a global controller exception handle@ControllerAdvice // this is a controller advice, it will handle
public class CustomErrorController {


    @ExceptionHandler(MethodArgumentNotValidException.class) // this is the exception we want to handle
    ResponseEntity handleBindError(MethodArgumentNotValidException e){

        // here we can make custom error message to make the returned error message more readable
        List errorList = e.getFieldErrors().stream() // stream() is a new feature in Java 8, it is a sequence of elements supporting sequential and parallel aggregate operations
                .map(fieldError -> {
                    Map<String,String> errorMap = new HashMap<>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errorMap;
                }).collect(Collectors.toList()); // collect() is a terminal operation, it's generally used to return a List, Set or Map


        return ResponseEntity.badRequest().body(errorList);
    }


    @ExceptionHandler // this is the JPA constraint violation exception handler
    ResponseEntity handleJPAViolations (TransactionSystemException exception){
        ResponseEntity.BodyBuilder responseEntity = ResponseEntity.badRequest(); // .badRequest(): This is a static method provided by ResponseEntity. It returns a ResponseEntity.BodyBuilder which is a builder that allows you to further configure the response.

        if(exception.getCause().getCause() instanceof ConstraintViolationException){
            ConstraintViolationException ve = (ConstraintViolationException) exception.getCause().getCause();
            List errorList = ve.getConstraintViolations().stream()
                    .map(constraintViolation -> {
                        Map<String,String> errorMap = new HashMap<>();
                        errorMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                        return errorMap;
                    }).collect(Collectors.toList());
            return responseEntity.body(errorList);
        }
        return responseEntity.build();
    }
}
