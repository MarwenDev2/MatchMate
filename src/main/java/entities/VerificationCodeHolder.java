package entities;

public class VerificationCodeHolder {
    private static String expectedCode;

    public static String getExpectedCode() {
        return expectedCode;
    }

    public static void setExpectedCode(String code) {
        expectedCode = code;
    }
}
