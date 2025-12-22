package com.swarang.portfolio_service.exception;


import com.swarang.portfolio_service.dto.ErrorResponseDto;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SimulationNotStartedException.class)
    public Mono<ErrorResponseDto> handleSimulationNotStartedException(SimulationNotStartedException ex){
        ErrorResponseDto response = ErrorResponseDto.builder()
                .message(ex.getMessage())
                .error(HttpStatusCode.valueOf(HttpStatus.SC_BAD_REQUEST).toString())
                .status(HttpStatus.SC_BAD_REQUEST)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return Mono.fromSupplier(() -> response);
    }

    @ExceptionHandler(PortfolioNotFoundException.class)
    public Mono<ErrorResponseDto> handlePortfolioNotFoundException(PortfolioNotFoundException ex){
        ErrorResponseDto response = ErrorResponseDto.builder()
                .message(ex.getMessage())
                .error(HttpStatusCode.valueOf(HttpStatus.SC_BAD_REQUEST).toString())
                .status(HttpStatus.SC_BAD_REQUEST)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return Mono.fromSupplier(() -> response);
    }
}
