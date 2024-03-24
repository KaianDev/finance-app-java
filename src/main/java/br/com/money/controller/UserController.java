package br.com.money.controller;

import br.com.money.exception.RandomException;
import br.com.money.model.User;
import br.com.money.model.dto.*;
import br.com.money.repository.UserRepository;
import br.com.money.service.EmailService;
import br.com.money.service.TokenConvert;
import br.com.money.service.TokenService;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @Value("${spring.url.forgot}")
    private String urlForgot;

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
        Map<String, Object> propMap = new HashMap<>();
        propMap.put("nome", user.getName());
        propMap.put("codigo", user.getCode());
        this.emailService.sendEmail(user.getEmail(), user.getCode(), propMap);
        String activationToken = this.tokenService.unauthorizedUserToken(user);
        return new ResponseEntity<CreateAccountResponseDto>(new CreateAccountResponseDto(user, activationToken), HttpStatus.CREATED);
    }
    @PostMapping("/confirm")
    public void confirmAccount(@RequestBody CodeRequestDto code, @RequestHeader String activationToken) {
        DecodedJWT tokenDecoded = this.tokenService.decoded(activationToken);
        User user = new User();
        user.setName(tokenDecoded.getClaim("name").asString());
        user.setEmail(tokenDecoded.getClaim("email").asString());
        user.setPassword(tokenDecoded.getClaim("password").asString());
        user.setStatus(false);
        user.setCode(tokenDecoded.getClaim("code").asString());
        if(!user.getCode().equals(code.code())) {
            throw new RandomException("Code not valid");
        }
        user.setStatus(true);
        user.setCode(null);
        this.userRepository.save(user);
    }

    @PostMapping("/resend")
    public void resendMail(@RequestHeader String activationToken) {
        var name = this.tokenService.decoded(activationToken).getClaim("name").asString();
        var email = this.tokenService.decoded(activationToken).getClaim("email").asString();
        var code = this.tokenService.decoded(activationToken).getClaim("code").asString();
        Map<String, Object> propMap = new HashMap<>();
        propMap.put("nome", name);
        propMap.put("codigo", code);
        this.emailService.sendEmail(email, code, propMap);
    }
    @PostMapping("/forgot/sendmail")
    public void forgotPasswordResend(@RequestBody ResendRequestDto resendRequestDto) {
        var user = this.userRepository.findByEmail(resendRequestDto.email());
        if(user == null) {
            throw new RandomException("Email not valid");
        }
        var code = UUID.randomUUID().toString();
        user.setCode(code);
        this.userRepository.save(user);
        var url = urlForgot + "/" + code + "/" + user.getId();

        Map<String, Object> propMap = new HashMap<>();
        propMap.put("nome", user.getName());
        propMap.put("url", url);
        this.emailService.sendEmailForgot(user.getEmail(), propMap);
    }

    @PostMapping("/forgot")
    public void forgotPassword(@RequestBody ForgotPasswordDto forgotPassword) {
        var user = this.userRepository.findById(forgotPassword.id());
        if(user.isEmpty()) {
            throw new RandomException("User does not exists");
        }
        if(!user.get().getCode().equals(forgotPassword.code())) {
            throw new RandomException("Code not valid");
        }

        user.get().setPassword(passwordEncoder.encode(forgotPassword.password()));
        this.userRepository.save(user.get());
    }

}
