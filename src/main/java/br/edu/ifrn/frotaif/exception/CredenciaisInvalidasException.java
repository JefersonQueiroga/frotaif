package br.edu.ifrn.frotaif.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Login recusado: e-mail inexistente, senha errada ou usuário inativo (sempre a mesma mensagem,
// para não revelar quais e-mails existem no sistema)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
