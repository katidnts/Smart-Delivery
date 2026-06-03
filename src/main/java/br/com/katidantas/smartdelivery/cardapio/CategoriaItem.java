package br.com.katidantas.smartdelivery.cardapio;

public enum CategoriaItem {

    PRATO_INDIVIDUAL("Prato Individual"),
    ENTRADA("Entrada"),
    SOBREMESA("Sobremesa"),
    BEBIDAS("Bebidas"),
    PRATO_PARA_DOIS("Prato para Dois"),
    PRATO_INFANTIL("Prato Infantil"),
    MASSA("Massa"),
    SALADAS("Saladas");

    private final String label;

    CategoriaItem(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
