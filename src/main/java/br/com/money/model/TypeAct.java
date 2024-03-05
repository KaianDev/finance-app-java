package br.com.money.model;

public enum TypeAct {
    REVENUE("Entrada"),
    EXPENSE("Saída");

    private String typeValue;

    private TypeAct(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeValue() {
        return this.typeValue;
    }
}
