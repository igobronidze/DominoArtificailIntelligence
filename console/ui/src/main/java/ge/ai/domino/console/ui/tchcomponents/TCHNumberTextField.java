package ge.ai.domino.console.ui.tchcomponents;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

public class TCHNumberTextField extends TCHTextField {

    private final NumberFormat nf;

    private final ObjectProperty<BigDecimal> number = new SimpleObjectProperty<>();

    public final BigDecimal getNumber() {
        return number.get();
    }

    public final void setNumber(BigDecimal value) {
        number.set(value);
    }

    public ObjectProperty<BigDecimal> numberProperty() {
        return number;
    }

    public TCHNumberTextField(TCHComponentSize size) {
        this(BigDecimal.ZERO, size);
    }

    public TCHNumberTextField(BigDecimal value, TCHComponentSize size) {
        this(value, NumberFormat.getInstance(), size);
        initHandlers();
    }

    public TCHNumberTextField(BigDecimal value, NumberFormat nf, TCHComponentSize size) {
        super(size);
        this.nf = nf;
        initHandlers();
        setNumber(value);
    }

    private void initHandlers() {
        setOnAction(arg0 -> parseAndFormatInput());
        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                parseAndFormatInput();
            }
        });
        numberProperty().addListener((obserable, oldValue, newValue) -> {
            setText(nf.format(newValue));
        });
    }

    private void parseAndFormatInput() {
        try {
            String input = getText();
            if (input == null || input.length() == 0) {
                return;
            }
            Number parsedNumber = nf.parse(input);
            BigDecimal newValue = new BigDecimal(parsedNumber.toString());
            setNumber(newValue);
            selectAll();
        } catch (ParseException ex) {
            setText(nf.format(number.get()));
        }
    }
}