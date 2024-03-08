package br.com.money.model;

public enum TypeAct {
    REVENUE("Receita"),
    EXPENSE("Despesa");

    private String typeValue;

    private TypeAct(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeValue() {
        return this.typeValue;
    }
}
