package Enum;

public enum RequisitionStatus {
    NEW("НОВАЯ"),
    IN_PROGRESS("В ОБРАБОТКЕ"),
    COMPLETED("ЗАВЕРШЕНА"),
    CANCELED("ОТМЕНЕНА");

    private final String str;

    RequisitionStatus(String str) {
        this.str = str;
    }

    public String getString() {
        return str;
    }
}
