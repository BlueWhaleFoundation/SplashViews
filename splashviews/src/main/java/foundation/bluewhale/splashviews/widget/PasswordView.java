package foundation.bluewhale.splashviews.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.jakewharton.rxbinding2.view.RxView;
import foundation.bluewhale.splashviews.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by hongsungjun on 2017. 5. 26..
 */

public class PasswordView extends ConstraintLayout {

    public PasswordView(Context context) {
        super(context);
        initView(context);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (_disposables != null) {
            _disposables.clear();
            _disposables.dispose();
        }

        if (tv_pwd_1 != null && mTextWatcher1 != null)
            tv_pwd_1.removeTextChangedListener(mTextWatcher1);
        if (tv_pwd_2 != null && mTextWatcher2 != null)
            tv_pwd_2.removeTextChangedListener(mTextWatcher2);
        if (tv_pwd_3 != null && mTextWatcher3 != null)
            tv_pwd_3.removeTextChangedListener(mTextWatcher3);
        if (tv_pwd_4 != null && mTextWatcher4 != null)
            tv_pwd_4.removeTextChangedListener(mTextWatcher4);
        if (tv_pwd_5 != null && mTextWatcher5 != null)
            tv_pwd_5.removeTextChangedListener(mTextWatcher5);
        if (tv_pwd_6 != null && mTextWatcher6 != null)
            tv_pwd_6.removeTextChangedListener(mTextWatcher6);
        super.onDetachedFromWindow();
    }

    PublishSubject<Integer> callback_completed;
    protected CompositeDisposable _disposables;
    TextView tv_title;
    TextView tv_pwd_1, tv_pwd_2, tv_pwd_3, tv_pwd_4, tv_pwd_5, tv_pwd_6;
    TextWatcher mTextWatcher1, mTextWatcher2, mTextWatcher3, mTextWatcher4, mTextWatcher5, mTextWatcher6;
    TextView keypad_1, keypad_2, keypad_3, keypad_4, keypad_5, keypad_6, keypad_7, keypad_8, keypad_9, keypad_0;
    TextView tv_error;
    View keypad_delete_all, keypad_delete_one;
    View button_forgot;
    ArrayList<Integer> keyArray = new ArrayList<>();

    void initView(Context context) {
        _disposables = new CompositeDisposable();

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.widget_password_view, this, false);
        addView(view);

        tv_title = view.findViewById(R.id.tv_title);
        tv_error = view.findViewById(R.id.tv_error);

        tv_pwd_1 = view.findViewById(R.id.tv_pwd_1);
        tv_pwd_2 = view.findViewById(R.id.tv_pwd_2);
        tv_pwd_3 = view.findViewById(R.id.tv_pwd_3);
        tv_pwd_4 = view.findViewById(R.id.tv_pwd_4);
        tv_pwd_5 = view.findViewById(R.id.tv_pwd_5);
        tv_pwd_6 = view.findViewById(R.id.tv_pwd_6);

        keypad_1 = view.findViewById(R.id.keypad_1);
        keypad_2 = view.findViewById(R.id.keypad_2);
        keypad_3 = view.findViewById(R.id.keypad_3);
        keypad_4 = view.findViewById(R.id.keypad_4);
        keypad_5 = view.findViewById(R.id.keypad_5);
        keypad_6 = view.findViewById(R.id.keypad_6);
        keypad_7 = view.findViewById(R.id.keypad_7);
        keypad_8 = view.findViewById(R.id.keypad_8);
        keypad_9 = view.findViewById(R.id.keypad_9);
        keypad_0 = view.findViewById(R.id.keypad_0);
        keypad_delete_all = view.findViewById(R.id.keypad_delete_all);
        keypad_delete_one = view.findViewById(R.id.keypad_delete_one);

        callback_completed = PublishSubject.create();
        _disposables.add(callback_completed
                .throttleLast(200, TimeUnit.MILLISECONDS)
                //.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        if (list != null && list.size() == 6) {
                            StringBuilder sb = new StringBuilder();
                            for (int i : list) {
                                sb.append(i);
                            }
                            if (passwordListener != null)
                                passwordListener.onCompleted(sb.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.keypad_0) {
                    addNumber(keyArray.get(0));

                } else if (i == R.id.keypad_1) {
                    addNumber(keyArray.get(1));

                } else if (i == R.id.keypad_2) {
                    addNumber(keyArray.get(2));

                } else if (i == R.id.keypad_3) {
                    addNumber(keyArray.get(3));

                } else if (i == R.id.keypad_4) {
                    addNumber(keyArray.get(4));

                } else if (i == R.id.keypad_5) {
                    addNumber(keyArray.get(5));

                } else if (i == R.id.keypad_6) {
                    addNumber(keyArray.get(6));

                } else if (i == R.id.keypad_7) {
                    addNumber(keyArray.get(7));

                } else if (i == R.id.keypad_8) {
                    addNumber(keyArray.get(8));

                } else if (i == R.id.keypad_9) {
                    addNumber(keyArray.get(9));

                } else if (i == R.id.keypad_delete_all) {
                    deleteAll();

                } else if (i == R.id.keypad_delete_one) {
                    deleteOne();

                }
            }
        };

        keypad_0.setOnClickListener(clickListener);
        keypad_1.setOnClickListener(clickListener);
        keypad_2.setOnClickListener(clickListener);
        keypad_3.setOnClickListener(clickListener);
        keypad_4.setOnClickListener(clickListener);
        keypad_5.setOnClickListener(clickListener);
        keypad_6.setOnClickListener(clickListener);
        keypad_7.setOnClickListener(clickListener);
        keypad_8.setOnClickListener(clickListener);
        keypad_9.setOnClickListener(clickListener);
        keypad_delete_all.setOnClickListener(clickListener);
        keypad_delete_one.setOnClickListener(clickListener);


        button_forgot = view.findViewById(R.id.button_forgot);
        _disposables.add(RxView.clicks(button_forgot)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(empty -> {
                    if (passwordListener != null)
                        passwordListener.onPasswordForgotClicked();
                }));
        /*setTextWatchers();
        setBackspace();*/

        shuffleArray();
        keypad_0.setText("" + keyArray.get(0));
        keypad_1.setText("" + keyArray.get(1));
        keypad_2.setText("" + keyArray.get(2));
        keypad_3.setText("" + keyArray.get(3));
        keypad_4.setText("" + keyArray.get(4));
        keypad_5.setText("" + keyArray.get(5));
        keypad_6.setText("" + keyArray.get(6));
        keypad_7.setText("" + keyArray.get(7));
        keypad_8.setText("" + keyArray.get(8));
        keypad_9.setText("" + keyArray.get(9));
    }

    public void setVisibilityOfForgotButton(boolean show) {
        button_forgot.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    List<Integer> list;

    void addNumber(int num) {
        if (list == null)
            list = new ArrayList<>();
        if (list.size() < 6)
            list.add(num);

        print();

        if (list.size() == 6) {
            if (callback_completed != null)
                callback_completed.onNext(0);
            /*StringBuilder sb = new StringBuilder();
            for (int i : list) {
                sb.append(i);
            }
            if (passwordListener != null)
                passwordListener.onCompleted(sb.toString());*/
        }
    }

    public void deleteAll() {
        if (list != null && list.size() > 0) {
            list.clear();
        }

        print();
    }

    void deleteOne() {
        if (list != null && list.size() > 0) {
            list.remove(list.size() - 1);
        }

        print();
    }

    public void removePassword() {
        if (list != null) {
            list.clear();
            print();
        }
    }

    void print() {
        if (list == null)
            return;

        int size = list.size();
        if (size >= 1)
            tv_pwd_1.setText("●");
        else
            tv_pwd_1.setText("");

        if (size >= 2)
            tv_pwd_2.setText("●");
        else
            tv_pwd_2.setText("");

        if (size >= 3)
            tv_pwd_3.setText("●");
        else
            tv_pwd_3.setText("");

        if (size >= 4)
            tv_pwd_4.setText("●");
        else
            tv_pwd_4.setText("");

        if (size >= 5)
            tv_pwd_5.setText("●");
        else
            tv_pwd_5.setText("");

        if (size >= 6)
            tv_pwd_6.setText("●");
        else
            tv_pwd_6.setText("");
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setErrorMessage(int errorMessageRes){
        if(errorMessageRes==0)
            tv_error.setText("");
        else
            tv_error.setText(getContext().getString(errorMessageRes));
    }

    public interface PasswordListener {
        void onPasswordForgotClicked();

        void onCompleted(String password);
    }

    PasswordListener passwordListener;

    public void setPasswordListener(PasswordListener passwordListener) {
        this.passwordListener = passwordListener;
    }

    private void shuffleArray() {
        ArrayList<Integer> tmpList = new ArrayList<>();
        Random rnd = new Random();
        int rndInt = rnd.nextInt(362880);

        for (int i = 0; i < 10; i++) {
            tmpList.add(i);
        }

        for (int i = 0; i < 10; i++) {
            int tmpPosition = rndInt % tmpList.size();
            keyArray.add(tmpList.get(tmpPosition));
            tmpList.remove(tmpPosition);
        }
    }
}
