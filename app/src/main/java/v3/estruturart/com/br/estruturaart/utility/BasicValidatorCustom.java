package v3.estruturart.com.br.estruturaart.utility;

import android.widget.EditText;

import com.basgeekball.awesomevalidation.ValidationHolder;
import com.basgeekball.awesomevalidation.utility.ValidationCallback;
import com.basgeekball.awesomevalidation.validators.BasicValidator;

import java.util.regex.Matcher;

public class BasicValidatorCustom extends BasicValidator {

    private ValidationCallback mValidationCallback = new ValidationCallback() {
        @Override
        public void execute(ValidationHolder validationHolder, Matcher matcher) {
            validationHolder.getEditText().setError(validationHolder.getErrMsg());
        }
    };

    BasicValidatorCustom() {
    }

    @Override
    public boolean trigger() {
        return checkFields(mValidationCallback);
    }

    public boolean triggerElement(Object element, String message) {
        for (ValidationHolder validationHolder : mValidationHolderList) {
            if (validationHolder.isEditTextView() && ((Object)validationHolder.getEditText()).equals(element)) {
                EditText editText = validationHolder.getEditText();
                editText.setError(message);
                editText.requestFocus();
                editText.setSelection(editText.getText().length());
            }
        }

        return true;
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
            if (validationHolder.isEditTextView() && ((Object)validationHolder.getEditText()).equals(ebj)) {
                if (validationHolder.isSomeSortOfView()) {
                    validationHolder.resetCustomError();
                } else {
                    validationHolder.getEditText().setError(null);
                }
            }
        }
    }
}