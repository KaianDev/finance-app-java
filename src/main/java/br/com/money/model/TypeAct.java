package br.com.money.model;

public enum TypeAct {
    REVENUE("REVENUE"),
    EXPENSE("EXPENSE");

    private String typeValue;

    private TypeAct(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeValue() {
        return this.typeValue;
    }
}
