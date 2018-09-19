package v3.estruturart.com.br.estruturaart.utility;

import com.basgeekball.awesomevalidation.ValidationHolder;
import com.basgeekball.awesomevalidation.utility.ValidationCallback;
import java.util.regex.Matcher;

public class BasicValidatorCustom extends Validator {

    private ValidationCallback mValidationCallback = new ValidationCallback() {
        @Override
        public void execute(ValidationHolder validationHolder, Matcher matcher) {
            validationHolder.getEditText().setError(validationHolder.getErrMsg());
        }
    };

    private ValidationCallback mValidationCallbackElement = new ValidationCallback() {
        @Override
        public void execute(ValidationHolder validationHolder, Matcher matcher) {
            validationHolder.getEditText().setError(validationHolder.getErrMsg());
        }
    };

    @Override
    public boolean trigger() {
        return checkFields(mValidationCallback);
    }
    @Override
    public boolean triggerElement(Object element, String message) {
        for (ValidationHolder validationHolder : mValidationHolderList) {
            if (((Object)validationHolder.getEditText()).equals(ebj)) {
                EditText editText = validationHolder.getEditText();
                editText.setError(message);
                editText.requestFocus();
                editText.setSelection(editText.getText().length());
            }
        }

        return checkFieldsElement(mValidationCallbackElement, element);
    }

    @Override
    public void halt() {
        for (ValidationHolder validationHolder : mValidationHolderList) {
            if (validationHolder.isSomeSortOfView()) {
                validationHolder.resetCustomError();
            } else {
                validationHolder.getEditText().setError(null);
            }
        }
    }

    public void haltElement(Object ebj) {
        for (ValidationHolder validationHolder : mValidationHolderList) {
            if (((Object)validationHolder.getEditText()).equals(ebj)) {
                if (validationHolder.isSomeSortOfView()) {
                    validationHolder.resetCustomError();
                } else {
                    validationHolder.getEditText().setError(null);
                }
            }
        }
    }
}