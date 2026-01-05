package com.farmacia.desafiosjava.util;

import java.text.Normalizer;

public class StringUtils {

    /**
     * Normaliza uma string removendo acentos, convertendo para minúsculas
     * e removendo caracteres especiais extras
     */
    public static String normalizeString(String input) {
        if (input == null) {
            return null;
        }

        // Remove espaços extras no início e fim
        String normalized = input.trim();

        // Converte para minúsculas
        normalized = normalized.toLowerCase();

        // Remove acentos e caracteres diacríticos
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");

        // Remove espaços extras entre palavras (mantém apenas um espaço)
        normalized = normalized.replaceAll("\\s+", " ");

        return normalized;
    }
}

