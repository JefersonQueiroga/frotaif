package br.edu.ifrn.frotaif.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<CPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        // Campo vazio é responsabilidade do @NotBlank
        if (cpf == null || cpf.isBlank()) {
            return true;
        }
        // Somente 11 dígitos, e não todos iguais (ex.: 11111111111 passaria no cálculo)
        if (!cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(cpf, 9) == cpf.charAt(9) - '0'
                && digitoVerificador(cpf, 10) == cpf.charAt(10) - '0';
    }

    // Soma ponderada dos primeiros "quantidade" dígitos, com pesos decrescentes a partir de quantidade + 1
    private int digitoVerificador(String cpf, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (cpf.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
