package ge.ai.domino.console.ui.util.dialog;

import ge.ai.domino.console.transfer.dto.exception.DAIConsoleException;

public class DAIExceptionHandling {

    public static void handleException(DAIConsoleException ex) {
        WarnDialog.showWarnDialog(ex.getMessageKey());
    }
}
