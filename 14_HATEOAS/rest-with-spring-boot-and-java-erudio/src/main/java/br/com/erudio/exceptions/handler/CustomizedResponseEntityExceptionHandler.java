package br.com.erudio.exceptions.handler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.erudio.exceptions.ExceptionResponse;
import br.com.erudio.exceptions.RequiredObjectIsNullException;
import br.com.erudio.exceptions.ResourceNotFoundException;

@ControllerAdvice // Diz ao Spring que esse é um controller especial e que potencialmente todos os outros controllers irão usar recursos dele
@RestController  // Diz ao Spring que esse é um controller 
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	
	// Intercepta qualquer exceção de modo genérico e trata de forma elegante
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
		
		// Substitui aquela excessão feia por um JSON
		ExceptionResponse exceptionResponse = new ExceptionResponse(
				new Date(),
				ex.getMessage(),
				request.getDescription(false));
		// Retorna uma response com um status code genérico
		return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	// Intercepta as exceções que nós personalizamos
	@ExceptionHandler(ResourceNotFoundException.class)
	public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(Exception ex, WebRequest request) {
		
		// Substitui aquela excessão feia por um JSON
		ExceptionResponse exceptionResponse = new ExceptionResponse(
				new Date(),
				ex.getMessage(),
				request.getDescription(false));
		// Retorna uma response com um status code de NOT FOUND (podemos usar qualquer status code)
		return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
	}

	// Intercepta as exceções que nós personalizamos
	@ExceptionHandler(RequiredObjectIsNullException.class)
	public final ResponseEntity<ExceptionResponse> handleBadRequestExceptions(Exception ex, WebRequest request) {
		
		// Substitui aquela excessão feia por um JSON
		ExceptionResponse exceptionResponse = new ExceptionResponse(
				new Date(),
				ex.getMessage(),
				request.getDescription(false));
		// Retorna uma response com um status code de BAD_REQUEST (podemos usar qualquer status code)
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
}
