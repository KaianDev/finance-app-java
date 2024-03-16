package br.com.money.controller;

import br.com.money.exception.RandomException;
import br.com.money.model.User;
import br.com.money.model.dto.LoginDto;
import br.com.money.model.dto.TokenResponseDto;
import br.com.money.model.dto.ValidateRequestDto;
import br.com.money.model.dto.ValidateResponseDto;
import br.com.money.repository.UserRepository;
import br.com.money.service.TokenConvert;
import br.com.money.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenConvert tokenConvert;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginDto loginDto) {
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        User userAuth = (User) authentication.getPrincipal();
        return new ResponseEntity<TokenResponseDto>(new TokenResponseDto(this.tokenService.getToken(userAuth)), HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateResponseDto> validate(@RequestBody ValidateRequestDto token, HttpServletRequest request) {
        if(token == null) {
            throw new RuntimeException("token does not exist");
        }

        String subject = this.tokenService.getSubject(token.token());
        if(subject.isEmpty()) {
            throw new RandomException("Token invalid");
        }

        var email = this.tokenService.getSubject(token.token());
        var user = this.userRepository.findByEmail(email);
        var emailAuth = this.tokenService.getSubject(this.tokenConvert.convert(request));
        var userAuth = this.userRepository.findByEmail(emailAuth);

        var isValid = false;
        if(userAuth.equals(user)) {
            isValid = true;
        }
        return new ResponseEntity<ValidateResponseDto>(new ValidateResponseDto(isValid), HttpStatus.OK);
    }

    @PostMapping("/create")
    public String createUser(@RequestBody LoginDto loginDto) {
        User user = new User();
        user.setEmail(loginDto.email());
        user.setPassword(passwordEncoder.encode(loginDto.password()));
        this.userRepository.save(user);
        return "User created";
    }
}
