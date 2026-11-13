package br.ufscar.multiloja.util;

public final class Documentos {
    private Documentos() {}

    public static String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    public static boolean cpfValido(String valor) {
        String cpf = somenteDigitos(valor);
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) return false;
        for (int t = 9; t <= 10; t++) {
            int soma = 0;
            for (int i = 0; i < t; i++) soma += (cpf.charAt(i) - '0') * (t + 1 - i);
            int dv = ((soma * 10) % 11) % 10;
            if (dv != cpf.charAt(t) - '0') return false;
        }
        return true;
    }

    public static boolean cnpjValido(String valor) {
        String cnpj = somenteDigitos(valor);
        if (cnpj.length() != 14 || cnpj.chars().distinct().count() == 1) return false;
        int[] p1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] p2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        return digito(cnpj, p1) == cnpj.charAt(12) - '0' && digito(cnpj, p2) == cnpj.charAt(13) - '0';
    }

    private static int digito(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) soma += (cnpj.charAt(i) - '0') * pesos[i];
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
