package uk.gov.dwp.vault.domain;

import java.util.Arrays;
import java.util.List;

public class DecryptedValue {

    private final char[] clearText;

    public DecryptedValue(char[] clearText) {
        this.clearText = clearText;
    }

    public boolean matches(DecryptedValue otherPassword) {
        if (clearText.length != otherPassword.clearText.length) {
            return false;
        }

        for (int index = 0; index < clearText.length; index++) {
            if (clearText[index] != otherPassword.clearText[index]) {
                return false;
            }
        }

        return true;
    }

    public char[] getClearText() {
        return clearText;
    }

    public void erase() {
        erase(clearText);
    }

    public static void erase(char[] clearText) {
        if (clearText != null) {
            for (int index = 0; index < clearText.length; index++) {
                // TODO: Do multiple writes of random characters first?  How strict do we need to be here?
                clearText[index] = '\0';
            }
        }
    }
}
