package br.edu.ifrn.frotaif.config;

import br.edu.ifrn.frotaif.model.Usuario;
import br.edu.ifrn.frotaif.repository.UsuarioRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

// Depois que a assinatura e a validade do token foram conferidas, transforma o token no Usuario do banco.
// Por padrão o principal seria o próprio Jwt; com este conversor ele continua sendo o Usuario,
// então @AuthenticationPrincipal Usuario nos controllers segue funcionando.
// Os papéis vêm do banco (não do token): um usuário inativado ou que mudou de papel
// é barrado na hora, sem esperar o token expirar.
public class JwtUsuarioConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UsuarioRepository usuarioRepository;

    public JwtUsuarioConverter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // sub (subject) = e-mail, gravado pelo TokenService no login
        Usuario usuario = usuarioRepository.findByEmail(jwt.getSubject())
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new InvalidBearerTokenException("Usuário do token não existe ou está inativo"));
        return UsernamePasswordAuthenticationToken.authenticated(usuario, jwt, usuario.getAuthorities());
    }
}
