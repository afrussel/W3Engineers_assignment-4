package io.left.core.assignment.ui.edit_profile;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.left.core.assignment.data.helper.SharedPreferenceHelper;
import io.left.core.assignment.ui.base.BaseActivity;
import io.left.core.assignment.ui.theme_color_change.ThemeColorChangeActivity;
import io.left.core.util.R;


public class EditProfileActivity extends BaseActivity<EditProfileMvpView,EditProfilePresenter> implements EditProfileMvpView {




    @BindView(R.id.input_layout_first_name)
    TextInputLayout inputLayoutFirstName;



    @BindView(R.id.input_layout_last_name)
    TextInputLayout inputLayoutLastName;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.edit_text_fist_name)
    EditText editTextFistName;

    @BindView(R.id.edit_text_last_name)
    EditText editTextLastName;

    @BindView(R.id.btn_signup)
    Button btnSignUp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);



        editTextFistName.addTextChangedListener(new MyTextWatcher(editTextFistName));
        editTextLastName.addTextChangedListener(new MyTextWatcher(editTextLastName));


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }


    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateFirstName()) {
            return;
        }

        if (!validateLastName()) {
            return;
        }
        SharedPreferenceHelper.onInstance(this).setFirstName(editTextFistName.getText().toString());
        SharedPreferenceHelper.onInstance(this).setLastName(editTextLastName.getText().toString());


        Toast.makeText(getApplicationContext(), "Name update", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ThemeColorChangeActivity.class));

    }

    private boolean validateFirstName() {
        if (editTextFistName.getText().toString().trim().isEmpty()) {
            inputLayoutFirstName.setError(getString(R.string.err_msg_first_name));
            requestFocus(editTextFistName);
            return false;
        } else {
            inputLayoutFirstName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLastName() {
        if (editTextLastName.getText().toString().trim().isEmpty()) {
            inputLayoutLastName.setError(getString(R.string.err_msg_last_name));
            requestFocus(editTextLastName);
            return false;
        } else {
            inputLayoutLastName.setErrorEnabled(false);
        }

        return true;
    }




    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_text_fist_name:
                    validateFirstName();
                    break;
                case R.id.edit_text_last_name:
                    validateLastName();
                    break;

            }
        }
    }
    @Override
    protected EditProfilePresenter initPresenter() {
        return new EditProfilePresenter();
    }
}
