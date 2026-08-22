package com.taurustex.api.tools.converter;

public final class FrenchNumberToWords {

    private FrenchNumberToWords() {
        // Classe utilitaire non instanciable
    }

    private static final String[] UNITS = {
            "", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf",
            "dix", "onze", "douze", "treize", "quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf"
    };



    private static final String[] TENS = {
            "", "", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante-dix", "quatre-vingt", "quatre-vingt-dix"
    };


    public static String convert(long number) {
        if (number == 0) {
            return "zéro";
        }
        if (number == Long.MIN_VALUE) {
            // Cas limite non pris en charge (overflow de Math.abs) : hors périmètre pour des montants financiers
            throw new IllegalArgumentException("Nombre trop grand pour être converti : " + number);
        }
        if (number < 0) {
            return "moins " + convert(Math.abs(number));
        }
        return convertRecursive(number, true).trim().replaceAll("\\s+", " ");
    }

    /**
     * @param n       le nombre à convertir
     * @param isFinal indique si ce groupe est en position finale (rien après lui dans la
     *                phrase). Détermine si "cent" / "quatre-vingts" prennent un "s".
     *                Règle : "cent" et "vingt" perdent leur "s" quand ils sont suivis
     *                d'un autre déterminant numéral (ex: "mille"), mais le conservent
     *                s'ils sont suivis d'un nom comme "million"/"milliard".
     */
    private static String convertRecursive(long n, boolean isFinal) {
        if (n < 20) {
            return UNITS[(int) n];
        }
        if (n < 100) {
            int ten = (int) (n / 10);
            int unit = (int) (n % 10);

            // Gestion de 70-79 et 90-99
            if (ten == 7 || ten == 9) {
                int baseTen = ten - 1;
                int remainderUnit = 10 + unit;
                if (unit == 1 && ten == 7) {
                    return TENS[baseTen] + " et onze";
                }
                return TENS[baseTen] + "-" + UNITS[remainderUnit];
            }
            // Gestion de 80 et 80+
            if (ten == 8) {
                if (unit == 0) {
                    // "quatre-vingts" prend un "s" seulement en position finale
                    return isFinal ? "quatre-vingts" : "quatre-vingt";
                }
                return "quatre-vingt-" + UNITS[unit]; // Pas de "s" et pas de "et" pour 81
            }
            // Cas généraux : 20-69
            if (unit == 1) {
                return TENS[ten] + " et un";
            }
            return TENS[ten] + (unit > 0 ? "-" + UNITS[unit] : "");
        }
        if (n < 1000) {
            long hundred = n / 100;
            long remainder = n % 100;
            String hundredStr = (hundred == 1) ? "cent" : UNITS[(int) hundred] + " cent";

            // "cent" prend un "s" s'il est multiplié, qu'il n'y a pas de reste,
            // ET qu'il est en position finale (pas suivi de "mille")
            if (hundred > 1 && remainder == 0 && isFinal) {
                hundredStr += "s";
            }
            return hundredStr + (remainder > 0 ? " " + convertRecursive(remainder, true) : "");
        }
        if (n < 1_000_000) { // < 1 Million
            long thousand = n / 1000;
            long remainder = n % 1000;
            // "mille" est invariable (jamais de "s") et toujours précédé d'un
            // multiplicateur en position NON finale (donc pas de "s" sur cent/vingt)
            String thousandStr = (thousand == 1) ? "mille" : convertRecursive(thousand, false) + " mille";
            return thousandStr + (remainder > 0 ? " " + convertRecursive(remainder, true) : "");
        }
        if (n < 1_000_000_000L) { // < 1 Milliard
            long million = n / 1_000_000;
            long remainder = n % 1_000_000;
            // "million" est un nom : le multiplicateur reste en position finale (garde son "s")
            String millionStr = (million == 1) ? "un million" : convertRecursive(million, true) + " millions";
            return millionStr + (remainder > 0 ? " " + convertRecursive(remainder, true) : "");
        }
        // >= 1 Milliard
        long billion = n / 1_000_000_000L;
        long remainder = n % 1_000_000_000L;
        String billionStr = (billion == 1) ? "un milliard" : convertRecursive(billion, true) + " milliards";
        return billionStr + (remainder > 0 ? " " + convertRecursive(remainder, true) : "");
    }


    /**
     * Formate directement pour le DTO Jasper
     * Exemple : 350000, "Francs CFA" -> "Trois cent cinquante mille Francs CFA"
     */
    public static String convertToFormattedAmount(long amount, String currency) {
        String text = convert(amount);
        if (text.isEmpty()) {
            return "";
        }
        text = Character.toUpperCase(text.charAt(0)) + text.substring(1);
        return (currency != null && !currency.trim().isEmpty()) ? text + " " + currency : text;
    }



}