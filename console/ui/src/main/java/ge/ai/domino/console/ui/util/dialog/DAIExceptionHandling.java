package ge.ai.domino.console.ui.util.dialog;

import ge.ai.domino.domain.exception.DAIException;

public class DAIExceptionHandling {

    public static void handleException(DAIException ex) {
        WarnDialog.showWarnDialog(ex.getMessageKey());
    }
}
