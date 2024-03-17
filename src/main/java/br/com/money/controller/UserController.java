package br.com.money.controller;

import br.com.money.exception.RandomException;
import br.com.money.model.User;
import br.com.money.model.dto.*;
import br.com.money.repository.UserRepository;
import br.com.money.service.EmailService;
import br.com.money.service.TokenConvert;
import br.com.money.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private EmailService emailService;

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
    public ResponseEntity<CreateAccountResponseDto> createUser(@RequestBody CreateAccountRequestDto createDto) {
        User user = new User();
        user.setName(createDto.name());
        user.setEmail(createDto.email());
        user.setPassword(passwordEncoder.encode(createDto.password()));
        user.setStatus(false);
        user.setCode(LocalDateTime.now().format(DateTimeFormatter.ofPattern("mmssSS")));
        this.userRepository.save(user);
        Map<String, Object> propMap = new HashMap<>();
        propMap.put("nome", user.getName());
        propMap.put("codigo", user.getCode());
        this.emailService.sendEmail(user.getEmail(), user.getCode(), propMap);
        return new ResponseEntity<CreateAccountResponseDto>(new CreateAccountResponseDto(user), HttpStatus.CREATED);
    }
    @PostMapping("/confirm")
    public void confirmAccount(@RequestBody CodeRequestDto code) {
        List<User> user = this.userRepository.findByCode(code.code());

        if(user.isEmpty()) {
            throw new RandomException("Code not valid");
        }
        if(user.size() > 1) {
            throw new RandomException("Code duplicate");
        }
        var userAuth = user.get(0);
        userAuth.setStatus(true);
        userAuth.setCode(null);
        this.userRepository.save(userAuth);
    }

    @PostMapping("/resend")
    public void resendMail(@RequestBody ResendRequestDto resendRequestDto) {
        var user = this.userRepository.findByEmail(resendRequestDto.email());
        if(user == null) {
            throw new RandomException("User not valid");
        }
        user.setCode(LocalDateTime.now().format(DateTimeFormatter.ofPattern("mmssSS")));
        this.userRepository.save(user);

        Map<String, Object> propMap = new HashMap<>();
        propMap.put("nome", user.getName());
        propMap.put("codigo", user.getCode());
        this.emailService.sendEmail(user.getEmail(), user.getCode(), propMap);
    }
}
