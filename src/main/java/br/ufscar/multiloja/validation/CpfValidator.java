package br.ufscar.multiloja.validation;

import br.ufscar.multiloja.util.Documentos;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<Cpf, String> {
    @Override
    public boolean isValid(String valor, ConstraintValidatorContext ctx) {
        return valor == null || valor.isBlank() || Documentos.cpfValido(valor);
    }
}
