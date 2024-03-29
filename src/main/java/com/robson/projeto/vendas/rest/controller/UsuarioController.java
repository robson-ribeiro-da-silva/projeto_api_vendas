package com.robson.projeto.vendas.rest.controller;

import com.robson.projeto.vendas.domain.entity.Usuario;
import com.robson.projeto.vendas.exception.SenhaInvalidaException;
import com.robson.projeto.vendas.rest.dto.CredenciaisDTO;
import com.robson.projeto.vendas.rest.dto.TokenDTO;
import com.robson.projeto.vendas.security.jwt.JwtService;
import com.robson.projeto.vendas.service.impl.UsuarioServiceImpl;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios/")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario salvar(@RequestBody @Valid Usuario usuario) {

        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        return usuarioService.salvar(usuario);
    }

    @PostMapping("/auth")
    public TokenDTO autenticar(@RequestBody CredenciaisDTO credenciaisDTO) {

        try {
            Usuario usuario = Usuario
                .builder()
                .login(credenciaisDTO.getLogin())
                .senha(credenciaisDTO.getSenha())
                .build();

            UserDetails usuarioAutenticado = usuarioService.autenticar(usuario);

            String token = jwtService.gerarToken(usuario);

            return new TokenDTO(usuario.getLogin(), token);

        } catch (UsernameNotFoundException | SenhaInvalidaException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
