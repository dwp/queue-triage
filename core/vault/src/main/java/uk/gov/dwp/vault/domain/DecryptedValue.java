package uk.gov.dwp.vault.domain;

public class DecryptedValue {

    private final char[] clearText;

    public DecryptedValue(char[] clearText) {
        this.clearText = clearText;
    }

    public char[] getClearText() {
        return clearText;
    }

    public void erase() {
        erase(clearText);
    }

    private void erase(char[] clearText) {
        if (clearText != null) {
            for (int index = 0; index < clearText.length; index++) {
                // TODO: Do multiple writes of random characters first?  How strict do we need to be here?
                clearText[index] = '\0';
            }
        }
    }
}
