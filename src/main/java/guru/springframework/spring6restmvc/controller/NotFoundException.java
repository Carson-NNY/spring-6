package guru.springframework.spring6restmvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Carson
 * @Version
 */
// 通常针对 Rest route的exception, 这个文件就够了(不用 ExceptionController 作为global handler)
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason = "Value Not Found")
public class NotFoundException extends RuntimeException{

    // control + enter 创建 constructors

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
