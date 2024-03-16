package br.com.money.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class TokenConvert {
    public String convert(HttpServletRequest request) {
        return request.getHeader("Authorization").replace("Bearer ", "");
    }
}
